package com.easylive.component;

import com.easylive.config.AppConfig;
import com.easylive.constants.Constants;
import com.easylive.entity.dto.SysSettingDto;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.dto.UploadingFileDto;
import com.easylive.enums.DateTimePatternEnum;
import com.easylive.entity.po.CategoryInfo;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.redis.RedisUtils;
import com.easylive.utils.DateUtil;
import com.easylive.utils.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class RedisComponent {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AppConfig appConfig;

    //获取系统设置信息
    public SysSettingDto getSysSetting() {
        SysSettingDto sysSettingDto = (SysSettingDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTING);
        if (sysSettingDto == null) {
            sysSettingDto = new SysSettingDto();
        }
        return sysSettingDto;
    }

    public String saveCheckCode(String checkCode){
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey,checkCode,Constants.REDIS_KEY_EXPIRE_ONE_MINUTE * 10);
        return checkCodeKey;
    }

    public String getCheckCode( String checkCodeKey) {
       return (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void saveTokenInfo4Web(TokenUserInfoDto tokenUserInfoDto) {
        String token = UUID.randomUUID().toString();
        tokenUserInfoDto.setExpireTime(System.currentTimeMillis() + Constants.REDIS_KEY_EXPIRE_ONE_DAY * 7);
        tokenUserInfoDto.setToken(token);
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_WEB + token, tokenUserInfoDto, Constants.REDIS_KEY_EXPIRE_ONE_DAY * 7);
    }

    public void cleanCheckCode( String checkCodeKey) {
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }


    public void cleanTokenInfo4Web(String token) {
        redisUtils.delete(Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    public TokenUserInfoDto getTokenInfo4Web(String token) {
       return (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_TOKEN_WEB + token);
    }

    public String saveTokenInfo4Admin(String account) {
        String token = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_ADMIN + token, account, Constants.REDIS_KEY_EXPIRE_ONE_DAY);
        return token;
    }

    public List<CategoryInfo> getCategoryInfo() {
        return (List<CategoryInfo>) redisUtils.get(Constants.REDIS_KEY_CATEGORY_LIST);
    }


    public void saveCategoryInfo(List<CategoryInfo> categoryInfoList) {
        redisUtils.set(Constants.REDIS_KEY_CATEGORY_LIST, categoryInfoList);
    }

    public String saveVideoFileInfo(String userId, String fileName, Integer chunks) {
        String uploadId = StringTools.getRandomLetters(Constants.LENGTH_10);
        UploadingFileDto uploadingFileDto = new UploadingFileDto();
        uploadingFileDto.setUploadId(uploadId);
        uploadingFileDto.setFileName(fileName);
        uploadingFileDto.setChunks(chunks);
        uploadingFileDto.setChunkIndex(0);
        String day = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String filePath = day + "/" + userId + uploadId;
        uploadingFileDto.setFilePath(filePath);  // 设置filePath到UploadingFileDto对象中
        String fileFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + filePath;
        File file = new File(fileFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        redisUtils.setex(Constants.REDIS_KEY_UPLOADING_FILE + userId + uploadId, uploadingFileDto, Constants.REDIS_KEY_EXPIRE_ONE_DAY);
        return uploadId;
    }

    public UploadingFileDto getVideoFileInfo(String UserId, String uploadId) {
        return (UploadingFileDto) redisUtils.get(Constants.REDIS_KEY_UPLOADING_FILE + UserId + uploadId);
    }

    //更新上传文件信息
    public void updateVideoFileInfo(String userId, UploadingFileDto uploadingFileDto) {
        redisUtils.setex(Constants.REDIS_KEY_UPLOADING_FILE + userId + uploadingFileDto.getUploadId(), uploadingFileDto, Constants.REDIS_KEY_EXPIRE_ONE_DAY);
    }


    //添加文件到删除队列
    public void addFile2DelQueue(String videoId, List<String> deleteFilePathList) {
        redisUtils.lpushAll(Constants.REDIS_KEY_FILE_DEL + videoId, deleteFilePathList, Constants.REDIS_KEY_EXPIRE_ONE_DAY * 7);
    }

    //添加文件到转码队列
    public void addFile2TransferQueue(List<VideoInfoFilePost> addList) {
        redisUtils.lpushAll(Constants.REDIS_KEY_QUEUE_TRANSFER, addList, 0);
    }

    //获取转码队列文件
    public VideoInfoFilePost getFileFromTransferQueue() {
        return (VideoInfoFilePost) redisUtils.rpop(Constants.REDIS_KEY_QUEUE_TRANSFER);
    }


    public void deleteVideoFileInfo(String userId,String uploadId) {
         redisUtils.delete(Constants.REDIS_KEY_UPLOADING_FILE + userId + uploadId);
    }

    public List<String> getFileFormDelQueue(String videoId) {
        return redisUtils.getQueueList(Constants.REDIS_KEY_FILE_DEL + videoId);
    }

    //清空删除队列
    public void cleanDelQueue(String videoId) {
        redisUtils.delete(Constants.REDIS_KEY_FILE_DEL + videoId);
    }

    public Integer  VideoPlayOnline(@NotEmpty String fileId, @NotEmpty String deviceId) {

        String userPlayOnlineKey = String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER, fileId, deviceId);
        String playOnlineCountKey = String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE, fileId);

        // 当前用户首次打开该文件
        if (!redisUtils.keyExists(userPlayOnlineKey)) {
            redisUtils.setex(userPlayOnlineKey, fileId, Constants.REDIS_KEY_EXPIRE_ONE_SECOND * 8);
            return redisUtils.incrementex(playOnlineCountKey, Constants.REDIS_KEY_EXPIRE_ONE_SECOND * 10).intValue();
        }

        // 续期
        redisUtils.expire(playOnlineCountKey, Constants.REDIS_KEY_EXPIRE_ONE_SECOND * 10);
        redisUtils.expire(userPlayOnlineKey, Constants.REDIS_KEY_EXPIRE_ONE_SECOND * 8);
        Integer count = (Integer) redisUtils.get(playOnlineCountKey);

        return count == null ? 1 : count;

    }
}
