package com.easylive.web.controller;

import com.easylive.entity.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/interact")
@Validated
@Slf4j
public class interactConrtoller  extends ABaseController{

    @RequestMapping("/online/reportVideoPlayOnline")
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId,@NotEmpty String deviceId) {
        return getSuccessResponseVO(null);
    }

}
