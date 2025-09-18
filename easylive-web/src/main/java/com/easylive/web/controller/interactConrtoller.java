package com.easylive.web.controller;

import com.easylive.component.RedisComponent;
import com.easylive.entity.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/interact")
@Validated
@Slf4j
public class interactConrtoller  extends ABaseController{

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/online/reportVideoPlayOnline")
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId,@NotEmpty String deviceId) {
        return getSuccessResponseVO(redisComponent.VideoPlayOnline(fileId, deviceId));
    }

}
