package com.easylive.web.controller;

import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.entity.po.VideoInfoPost;
import com.easylive.entity.query.VideoInfoPostQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.entity.vo.VideoStatusCountInfoVO;
import com.easylive.enums.VideoStatusEnum;
import com.easylive.service.VideoInfoPostService;
import com.easylive.utils.JsonUtils;
import com.easylive.web.annotation.GlobalInterceptor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/ucenter/post")
@Validated
@Slf4j
public class UcenterVideoPostController extends  ABaseController{

    @Resource
    private VideoInfoPostService videoInfoPostService;



    //发布视频
    @RequestMapping("/postVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getImage(String videoId,
                               @NotEmpty String videoCover,
                               @NotEmpty @Size(max = 100) String videoName,
                               @NotNull Integer pCategoryId,
                               Integer categoryId,
                               @NotNull Integer postType,
                               @NotEmpty @Size(max = 300) String tags,
                               @Size(max = 2000) String introduction,
                               @Size(max = 3) String interaction,
                               @NotEmpty String uploadFileList) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        //视频文件列表
        List<VideoInfoFilePost> videoInfoFilePostList = JsonUtils.convertJsonArray2List(uploadFileList, VideoInfoFilePost.class);

        //视频基本信息
        VideoInfoPost videoInfoPost = new VideoInfoPost();
        videoInfoPost.setVideoId(videoId);
        videoInfoPost.setVideoCover(videoCover);
        videoInfoPost.setVideoName(videoName);
        videoInfoPost.setpCategoryId(pCategoryId);
        videoInfoPost.setCategoryId(categoryId);
        videoInfoPost.setPostType(postType);
        videoInfoPost.setTags(tags);
        videoInfoPost.setInteraction( interaction);
        videoInfoPost.setIntroduction(introduction);
        videoInfoPost.setUserId(tokenUserInfoDto.getUserId());

        videoInfoPostService.savaVideoInfo(videoInfoPost, videoInfoFilePostList);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoList")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadVideoList(Integer status, Integer pageNo, String videoNameFuzzy) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();

        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        videoInfoPostQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoPostQuery.setOrderBy("v.create_time desc");
        videoInfoPostQuery.setPageNo(pageNo);

        //处理稿件查询
        if (status != null){
            //进行中
            if (status == -1){
                videoInfoPostQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(), VideoStatusEnum.STATUS4.getStatus()});
            }else {
                videoInfoPostQuery.setStatus(status);
            }
        }

        //设置其他查询条件
        videoInfoPostQuery.setVideoNameFuzzy(videoNameFuzzy);
        videoInfoPostQuery.setCountInfo(true);

        //调用service执行查询
        PaginationResultVO paginationResultVo = videoInfoPostService.findListByPage(videoInfoPostQuery);
        return getSuccessResponseVO(paginationResultVo);
    }

    //获取视频数量
    @RequestMapping("/getVideoCountInfo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getVideoCountInfo() {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        //已上传
        videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS3.getStatus());
        videoInfoPostQuery.setUserId(tokenUserInfo.getUserId());
        Integer auditSuccessCount = videoInfoPostService.findCountByParam(videoInfoPostQuery);
        //上传失败
        videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS4.getStatus());
        Integer auditFailCount = videoInfoPostService.findCountByParam(videoInfoPostQuery);
        //更新中
        videoInfoPostQuery.setStatus(null);
        videoInfoPostQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(), VideoStatusEnum.STATUS4.getStatus()});
        Integer inProcessCount = videoInfoPostService.findCountByParam(videoInfoPostQuery);
        VideoStatusCountInfoVO videoStatusCountInfoVo = new VideoStatusCountInfoVO();
        videoStatusCountInfoVo.setAuditSuccessCount(auditSuccessCount);
        videoStatusCountInfoVo.setAuditFailCount(auditFailCount);
        videoStatusCountInfoVo.setInProcessCount(inProcessCount);
        return getSuccessResponseVO(videoStatusCountInfoVo);
    }

    


    @RequestMapping("/delUploadVideo ")
    public ResponseVO delUploadVideo(@NotEmpty String videoId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        return getSuccessResponseVO(videoInfoPostService.deleteVideoInfoPostByVideoId(videoId));
    }
}
