package com.easylive.admin.controller;

import com.easylive.config.AppConfig;
import com.easylive.constants.Constants;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.enums.DateTimePatternEnum;
import com.easylive.enums.ResponseEnum;
import com.easylive.exception.BusinessException;
import com.easylive.service.VideoInfoFilePostService;
import com.easylive.utils.DateUtil;
import com.easylive.utils.FFmpegUtils;
import com.easylive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
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
    private FFmpegUtils ffmpegUtils;

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotNull MultipartFile file, @NotNull Boolean createThumbnail) throws IOException {
        String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_COVER + month;
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
        return getSuccessResponseVO(Constants.FILE_COVER + month + "/" + realFileName);
    }

    @RequestMapping("/getImage")
    public void getImage(HttpServletResponse response, @NotNull String sourceName) {
        if (!StringTools.pathIsOk(sourceName)) {
            throw new BusinessException(ResponseEnum.CODE_600);
        }
        String suffix = StringTools.getFileSuffix(sourceName);
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, sourceName);
    }

    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + filePath);
        if (!file.exists()) {
            return;
        }
        try (OutputStream outputStream = response.getOutputStream();
             FileInputStream inputStream = new FileInputStream(file)) {
            // 1024字节来读
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                // 读多少 写多少
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("读取文件失败", e);
        }
    }

//    @RequestMapping("/videoResource/{fileId}")
//    public Void videoResource(HttpServletResponse response, @PathVariable("fileId") @NotEmpty String fileId) {
//        VideoInfoFilePost videoInfoFilePost = videoInfoFilePostService.getVideoInfoFilePostByFileId(fileId);
//        String filePath = videoInfoFilePost.getFilePath();
//        readFile(response, filePath + "/" + Constants.M3U8_NAME);
//    }
//
//    @RequestMapping("/videoResource/{fileId}/{ts}")
//    public Void videoResourceTs(HttpServletResponse response, @PathVariable("fileId") @NotEmpty String fileId, @PathVariable("ts") @NotEmpty String ts) {
//        VideoInfoFilePost videoInfoFilePost = videoInfoFilePostService.getVideoInfoFilePostByFileId(fileId);
//        String filePath = videoInfoFilePost.getFilePath();
//        readFile(response, filePath + "/" + ts);
//    }
}
