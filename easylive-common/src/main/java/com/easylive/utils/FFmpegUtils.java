package com.easylive.utils;

import com.easylive.config.AppConfig;
import com.easylive.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */
@Slf4j
@Component
public class FFmpegUtils {

    @Resource
    private AppConfig appConfig;

    // 生成缩略图
    public void createThumbnail(String filePath) {
        String CMD = "ffmpeg -i \"%s\" -vf scale=200:-1 \"%s\"";
        CMD = String.format(CMD, filePath, filePath + Constants.IMAGE_THUMBNAIL_SUFFIX);
        ProcessUtils.executeCommand(CMD, appConfig.getShowFFmpegLog());
    }

    // 获取视频时长
    public Integer getVideoInfoDuration(String completeVideo) {
        final String CMD_GET_CODE = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 \"%s\"";
        String cmd = String.format(CMD_GET_CODE, completeVideo);
        String result = ProcessUtils.executeCommand(cmd, appConfig.getShowFFmpegLog());

        if (StringTools.isEmpty(result)) {
            return 0;
        }
        // 关键修复点：彻底清理无效字符并校验空值
        result = result.replace("\n", "").trim(); // 移除所有换行符和首尾空格
        if (result.isEmpty()) {
            return 0;
        }
        try {
            return new BigDecimal(result).intValue();
        } catch (NumberFormatException e) {
            // 添加详细日志，记录原始输出以便排查
            log.error("解析视频时长失败，ffprobe 输出内容: [{}]", result, e);
            return 0;
        }
    }

    // 获取视频编码格式
    public String getVideoCodec(String videoFilePath) {
        final String CMD_GET_CODE = "ffprobe -v error -select_streams v:0 -show_entries stream=codec_name \"%s\"";
        String cmd = String.format(CMD_GET_CODE, videoFilePath);
        String result = ProcessUtils.executeCommand(cmd, appConfig.getShowFFmpegLog());
        result = result.replace("\n", "");
        result = result.substring(result.indexOf("=") + 1);
        String codec = result.substring(0, result.indexOf("["));
        return codec;
    }

    // 转换视频编码格式
    public void convertHevc2Mp4(String newFileName, String videoFilePath) {
        String CMD_HEVC_264 = "ffmpeg -i \"%s\" -c:v libx264 -crf 20 \"%s\" -y";
        String cmd = String.format(CMD_HEVC_264, newFileName, videoFilePath);
        ProcessUtils.executeCommand(cmd, appConfig.getShowFFmpegLog());
    }

    // 转换视频编码格式
    public void convertVideo2Ts(File tsFolder, String videoFilePath) {
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i \"%s\"  -vcodec copy -acodec copy -bsf:v h264_mp4toannexb \"%s\"";
        final String CMD_CUT_TS = "ffmpeg -i \"%s\" -c copy -map 0 -f segment -segment_list \"%s\" -segment_time 10 %s/%%4d.ts";
        String tsPath = tsFolder + "/" + Constants.TS_NAME;

        // 生成 .ts 文件
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, appConfig.getShowFFmpegLog());

        // 生成索引文件 .m3u8 和切片 .ts
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + Constants.M3U8_NAME, tsFolder.getPath());
        ProcessUtils.executeCommand(cmd, appConfig.getShowFFmpegLog());

        // 删除 index.ts
        new File(tsPath).delete();
    }

}
