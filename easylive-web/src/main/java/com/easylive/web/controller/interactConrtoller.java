package com.easylive.web.controller;

import com.easylive.component.RedisComponent;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.UserAction;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserActionQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.enums.VideoOrderTypeEnum;
import com.easylive.service.UserActionService;
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

    @Resource
    private UserActionService userActionService;

    @RequestMapping("/online/reportVideoPlayOnline")
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId,@NotEmpty String deviceId) {
        return getSuccessResponseVO(redisComponent.VideoPlayOnline(fileId, deviceId));
    }

    /**
     * 用户收藏列表
     * @param userId
     * @param type
     * @param pageNo
     * @param videoName
     * @param orderType
     * @return
     */
    @RequestMapping("ucenter/home/loadUserCollection")
    public ResponseVO loadUserCollection(@NotEmpty String userId,
                                         Integer pageNo, String videoName){
        UserActionQuery userActionQuery = new UserActionQuery();
        userActionQuery.setUserId(userId);
        userActionQuery.setActionType(UserActionTypeEnum.VIDEO_COLLECT.getType());
        userActionQuery.setPageNo(pageNo);
        userActionQuery.setVideoIdFuzzy(videoName);
        userActionQuery.setQueryVideoInfo(true);
        userActionQuery.setPageSize(PageSize.SIZE15.getSize());
        PaginationResultVO<UserAction> paginationResultVo = userActionService.findListByPage(userActionQuery);

        return getSuccessResponseVO(paginationResultVo);
    }

}
