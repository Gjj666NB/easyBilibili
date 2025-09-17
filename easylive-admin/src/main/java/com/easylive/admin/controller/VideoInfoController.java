package com.easylive.admin.controller;

import java.util.List;

import com.easylive.entity.po.VideoInfoPost;
import com.easylive.entity.query.VideoInfoPostQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoInfoFilePostService;
import com.easylive.service.VideoInfoPostService;
import com.easylive.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 视频信息 Controller
 */
@RestController("videoInfoController")
@RequestMapping("/videoInfo")
public class VideoInfoController extends ABaseController{

	@Resource
	private VideoInfoService videoInfoService;

	@Resource
	private VideoInfoFilePostService videoInfoFilePostService;

	@Resource
	private VideoInfoPostService videoInfoPostService;

	@RequestMapping("/loadVideoList")
	public ResponseVO loadVideoList( VideoInfoPostQuery videoInfoPostQuery){
		videoInfoPostQuery.setOrderBy("v.last_update_time desc");
		videoInfoPostQuery.setCountInfo(true);
		videoInfoPostQuery.setUserInfo(true);
		PaginationResultVO paginationResultVO = videoInfoPostService.findListByPage(videoInfoPostQuery);
		return getSuccessResponseVO(paginationResultVO);
	}

	@RequestMapping("/auditVideo")
//	@MessageInterceptor(messageType = MessageTypeEnum.SYS)
	public ResponseVO auditVideo(@NotEmpty String videoId, @NotNull Integer status, String reason) {
		videoInfoPostService.auditVideo(videoId, status, reason);
		return getSuccessResponseVO(null);
	}



}