package com.easylive.web.task;

import com.easylive.component.RedisComponent;
import com.easylive.constants.Constants;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.service.VideoInfoPostService;
import com.easylive.service.VideoInfoService;
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
}