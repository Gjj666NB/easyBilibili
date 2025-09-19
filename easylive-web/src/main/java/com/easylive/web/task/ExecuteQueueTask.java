package com.easylive.web.task;

import com.easylive.component.RedisComponent;
import com.easylive.constants.Constants;
import com.easylive.entity.dto.VideoPlayInfoDto;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.service.VideoInfoPostService;
import com.easylive.service.VideoInfoService;
import com.easylive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ExecuteQueueTask {
    private ExecutorService executorService = Executors.newFixedThreadPool(Constants.TWO);

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private VideoInfoPostService videoInfoPostService;

//    @Resource
//    private VideoPlayHistoryService videoPlayHistoryService;

    @Resource
    private VideoInfoService videoInfoService;

//    @Resource
//    private EsSearchComponent esSearchComponent;

    @PostConstruct
    public void consumTransferFileQueue() {
        executorService.execute(() -> {
            while (true) {
                try {
                    VideoInfoFilePost videoInfoFilePost = redisComponent.getFileFromTransferQueue();
                    if (videoInfoFilePost == null) {
                        TimeUnit.SECONDS.sleep(2);
                        continue;
                    }
                    videoInfoPostService.transferVideoFile(videoInfoFilePost);
                } catch (Exception e) {
                    log.error("获取转码文件传输队列失败", e);
                }
            }
        });
    }


//    @PostConstruct
//    public void consumVideoPlayInfoQueue() {
//        executorService.execute(() -> {
//            while (true) {
//                try {
//                    VideoPlayInfoDto videoPlayInfoDto = redisComponent.getVideoPlayInfoQueue();
//                    if (videoPlayInfoDto == null) {
//                        TimeUnit.SECONDS.sleep(2);
//                        continue;
//                    }
//                    videoInfoService.addReadCount(videoPlayInfoDto.getVideoId());
//                    if (!StringTools.isEmpty(videoPlayInfoDto.getUserId())) {
//                        videoPlayHistoryService.saveVideoPlayHistory(videoPlayInfoDto.getUserId(), videoPlayInfoDto.getVideoId(), videoPlayInfoDto.getFileIndex());
//                    }
//                    //按天记录播放数
//                    redisComponent.recordPlayCount(videoPlayInfoDto.getVideoId());
//
//                    //更新es播放数量
//                    //esSearchComponent.updateDocCount(videoPlayInfoDto.getVideoId(), SearchOrderTypeEnum.VIDEO_PLAY.getField(), 1);
//                } catch (Exception e) {
//                    log.error("获取转码文件传输队列失败", e);
//                }
//            }
//        });
//    }
}