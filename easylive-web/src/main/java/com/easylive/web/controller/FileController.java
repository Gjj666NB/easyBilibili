package com.easylive.web.controller;

import com.easylive.component.RedisComponent;
import com.easylive.config.AppConfig;

import com.easylive.constants.Constants;
import com.easylive.entity.dto.SysSettingDto;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.dto.UploadingFileDto;
import com.easylive.enums.DateTimePatternEnum;
import com.easylive.entity.po.VideoInfoFile;

import com.easylive.entity.vo.ResponseVO;
import com.easylive.enums.ResponseEnum;
import com.easylive.exception.BusinessException;
import com.easylive.service.VideoInfoFileService;

import com.easylive.utils.DateUtil;
import com.easylive.utils.FFmpegUtils;
import com.easylive.utils.StringTools;

import com.easylive.web.annotation.GlobalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

@RestController
@RequestMapping("/file")
@Validated
@Slf4j
public class FileController extends ABaseController {

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private FFmpegUtils ffmpegUtils;

    @Resource
    private VideoInfoFileService videoInfoFileService;

    @RequestMapping("/getImage")
    public void getImage(HttpServletResponse response, @NotNull String sourceName) {
        if (!StringTools.pathIsOk(sourceName)) {
            throw new BusinessException(ResponseEnum.CODE_600);
        }
        String suffix = StringTools.getFileSuffix(sourceName);
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, sourceName);
        System.out.println("请求进来了，sourceName = " + sourceName);

    }

    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + filePath);
        if (!file.exists()) {
            return;
        }
        try (OutputStream outputStream = response.getOutputStream();
             FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("读取文件失败", e);
        }
    }

    //文件预上传
    @RequestMapping("/preUploadVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO preUploadVideo(@NotEmpty String fileName, @NotNull Integer chunks) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        String uploadId = redisComponent.saveVideoFileInfo(tokenUserInfoDto.getUserId(), fileName, chunks);
        return getSuccessResponseVO(uploadId);
    }


    //文件上传
    @RequestMapping("/uploadVideo")
    @GlobalInterceptor(checkLogin = true)
   public ResponseVO uploadVideo(@NotNull MultipartFile chunkFile, @NotNull Integer chunkIndex, @NotEmpty String uploadId) throws IOException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        //判断文件是否存在
        UploadingFileDto videoFileInfo = redisComponent.getVideoFileInfo(tokenUserInfoDto.getUserId(), uploadId);
        if (videoFileInfo == null) {
            throw new BusinessException("文件不存在,请重新上传");
        }
        //判断文件是否超出大小
        SysSettingDto sysSettingDto = redisComponent.getSysSetting();
        if (videoFileInfo.getFileSize() > sysSettingDto.getVideoSize() * Constants.MB_SIZE) {
            throw new BusinessException("文件大小超出限制");
        }
        //判断文件切片是否正常
        if ((chunkIndex - 1) > videoFileInfo.getChunkIndex() || chunkIndex > videoFileInfo.getChunks() - 1) {
            throw new BusinessException(ResponseEnum.CODE_600);
        }
        //上传文件到redis
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + videoFileInfo.getFilePath();
        File targetFile = new File(folder + "/" + chunkIndex);
        chunkFile.transferTo(targetFile);
        videoFileInfo.setChunkIndex(chunkIndex);
        videoFileInfo.setFileSize(videoFileInfo.getFileSize() + chunkFile.getSize());
        redisComponent.updateVideoFileInfo(tokenUserInfoDto.getUserId(), videoFileInfo);
        return getSuccessResponseVO(null);
   }


    //上传图片
    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotNull MultipartFile file, @NotNull Boolean createThumbnail) throws IOException {
        String day = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_COVER + day;
        File fileFolder = new File(folder);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String suffix = StringTools.getFileSuffix(fileName);
        String realFileName = StringTools.getRandomLetters(Constants.LENGTH_30) + suffix;
        String realFilePath = folder + "/" + realFileName;
        File realFile = new File(realFilePath);
        file.transferTo(realFile);
        if (createThumbnail) {
            ffmpegUtils.createThumbnail(realFilePath);
        }
        return getSuccessResponseVO(Constants.FILE_COVER + day + "/" + realFileName);
    }



    @RequestMapping("/videoResource/{fileId}/{ts}")
    public void videoResourceTs(HttpServletResponse response, @PathVariable("fileId") @NotEmpty String fileId, @PathVariable("ts") @NotEmpty String ts) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoInfoFileByFileId(fileId);
        String filePath = videoInfoFile.getFilePath();
        readFile(response, filePath + "/" + ts);
    }


    @RequestMapping("/delUploadVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delUploadVideo(@NotEmpty String uploadId) throws IOException {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UploadingFileDto videoFileInfo = redisComponent.getVideoFileInfo(tokenUserInfo.getUserId(), uploadId);
        if (videoFileInfo == null){
            throw new BusinessException("文件不存在");
        }
        redisComponent.deleteVideoFileInfo(tokenUserInfo.getUserId(), uploadId);
        FileUtils.deleteDirectory(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + videoFileInfo.getFilePath()));
        return getSuccessResponseVO(uploadId);
    }

    @RequestMapping("/videoResource/{fileId}")
    public void videoResource(HttpServletResponse response, @PathVariable("fileId") @NotEmpty String fileId) {
        //拿到文件路径
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoInfoFileByFileId(fileId);
        String filePath = videoInfoFile.getFilePath();
        //读取索引
        readFile(response, filePath + "/" + Constants.M3U8_NAME);

    }



}
