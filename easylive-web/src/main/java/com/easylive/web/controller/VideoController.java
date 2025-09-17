package com.easylive.web.controller;

import com.easylive.component.RedisComponent;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.enums.VideoRecommendTypeEnum;
import com.easylive.entity.po.*;
import com.easylive.entity.query.*;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.entity.vo.VideoInResultVO;
import com.easylive.enums.ResponseEnum;
import com.easylive.exception.BusinessException;
import com.easylive.service.UserActionService;
import com.easylive.service.VideoInfoFileService;
import com.easylive.service.VideoInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/video")
@Validated
@Slf4j
public class VideoController extends ABaseController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoFileService videoInfoFileService;

    @Resource
    private UserActionService userActionService;

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/loadCommendVideo")
    public ResponseVO loadCommendVideo() {
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setOrderBy("create_time desc");
        videoInfoQuery.setUserInfo(true);
        videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.RECOMMEND.getType());
        List<VideoInfo> recommendVideoInfoList = videoInfoService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO(recommendVideoInfoList);
    }

    @RequestMapping("/loadVideo")
    public ResponseVO loadVideo(Integer pCategoryId, Integer categoryId, Integer pageNo) {
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setpCategoryId(pCategoryId);
        videoInfoQuery.setCategoryId(categoryId);
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setUserInfo(true);
        videoInfoQuery.setOrderBy("v.create_time desc");
        videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.NO_RECOMMEND.getType());
        PaginationResultVO<VideoInfo> recommendVideoInfoList = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(recommendVideoInfoList);
    }

    @RequestMapping("/getVideoInfo")
    public ResponseVO getVideoInfo(@NotEmpty String videoId) {
        VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException(ResponseEnum.CODE_404);
        }

        VideoInResultVO videoInResultVO = new VideoInResultVO(videoInfo,new ArrayList());

        //加入用户操作查询
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        UserActionQuery userActionQuery = new UserActionQuery();
        userActionQuery.setUserId(tokenUserInfo.getUserId());
        userActionQuery.setVideoId(videoId);
        userActionQuery.setActionTypeArray(new Integer[]{UserActionTypeEnum.VIDEO_LIKE.getType(),
                UserActionTypeEnum.VIDEO_COLLECT.getType(), UserActionTypeEnum.VIDEO_COIN.getType()});
        List<UserAction> listByParam = userActionService.findListByParam(userActionQuery);

        videoInResultVO.setVideoInfo(videoInfo);
        videoInResultVO.setUserActionList(listByParam);
        return getSuccessResponseVO(videoInResultVO);
    }

    @RequestMapping("/loadVideoPList")
    public ResponseVO loadVideoPList(@NotEmpty String videoId){
        VideoInfoFileQuery videoInfoFileQuery = new VideoInfoFileQuery();
        videoInfoFileQuery.setVideoId(videoId);
        videoInfoFileQuery.setOrderBy("file_index asc");
        List<VideoInfoFile> videoInfoFilePostList = videoInfoFileService.findListByParam(videoInfoFileQuery);
        return getSuccessResponseVO(videoInfoFilePostList);
    }





}