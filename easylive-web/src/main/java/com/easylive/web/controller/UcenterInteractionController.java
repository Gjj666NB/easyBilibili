package com.easylive.web.controller;

import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.po.VideoDanmu;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.VideoCommentQuery;
import com.easylive.entity.query.VideoDanmuQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoCommentService;
import com.easylive.service.VideoDanmuService;
import com.easylive.service.VideoInfoService;
import com.easylive.web.annotation.GlobalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/ucenter/interaction")
@Slf4j
public class UcenterInteractionController extends ABaseController{

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoCommentService videoCommentService;
    
    @Resource
    private VideoDanmuService videoDanmuService;

    @RequestMapping("/loadAllVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadAllVideo(){
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setUserId(tokenUserInfo.getUserId());
        videoInfoQuery.setOrderBy("create_time desc");
        List<VideoInfo> list = videoInfoService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO( list);
    }

    @RequestMapping("/loadComment")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadComment(Integer pageNo, @NotEmpty String videoId){
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setVideoId(videoId);
        videoCommentQuery.setUserId(tokenUserInfo.getUserId());
        videoCommentQuery.setPageSize(pageNo);
        videoCommentQuery.setOrderBy("comment_id desc");
        videoCommentQuery.setQueryVideoInfo(true);
        PaginationResultVO<VideoComment> page = videoCommentService.findListByPage(videoCommentQuery);
        return getSuccessResponseVO( page);
    }

    @RequestMapping("/delComment")
    public ResponseVO delComment(@NotNull Integer commentId){
        videoCommentService.deleteComment(commentId, getTokenUserInfo().getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(Integer pageNo, @NotEmpty String videoId){
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
        videoDanmuQuery.setVideoId(videoId);
        videoDanmuQuery.setUserId(tokenUserInfo.getUserId());
        videoDanmuQuery.setPageSize(pageNo);
        videoDanmuQuery.setOrderBy("danmu_id desc");
        videoDanmuQuery.setQueryVideoInfo(true);
        PaginationResultVO<VideoDanmu> listByPage = videoDanmuService.findListByPage(videoDanmuQuery);
        return getSuccessResponseVO(listByPage);
    }

    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(@NotNull Integer danmuId){
        videoDanmuService.deleteDanmuByDanmuId(danmuId,getTokenUserInfo().getUserId());
        return getSuccessResponseVO(null);
    }

}
