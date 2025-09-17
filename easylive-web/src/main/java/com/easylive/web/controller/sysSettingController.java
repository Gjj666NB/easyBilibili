package com.easylive.web.controller;

import com.easylive.component.RedisComponent;
import com.easylive.entity.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@Validated
@RequestMapping("/sysSetting")
public class sysSettingController extends ABaseController{

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/getSysSetting")
    public ResponseVO getSysSetting(){
       return getSuccessResponseVO(redisComponent.getSysSetting());
    }
}
