package com.easylive.web.controller;

import java.util.List;

import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频信息 Controller
 */
@RestController("videoInfoController")
@RequestMapping("/videoInfo")
public class VideoInfoController extends ABaseController{

	@Resource
	private VideoInfoService videoInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(VideoInfoQuery query){
		return getSuccessResponseVO(videoInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(VideoInfo bean) {
		videoInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<VideoInfo> listBean) {
		videoInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<VideoInfo> listBean) {
		videoInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据VideoId查询对象
	 */
	@RequestMapping("/getVideoInfoByVideoId")
	public ResponseVO getVideoInfoByVideoId(String videoId) {
		return getSuccessResponseVO(videoInfoService.getVideoInfoByVideoId(videoId));
	}

	/**
	 * 根据VideoId修改对象
	 */
	@RequestMapping("/updateVideoInfoByVideoId")
	public ResponseVO updateVideoInfoByVideoId(VideoInfo bean,String videoId) {
		videoInfoService.updateVideoInfoByVideoId(bean,videoId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据VideoId删除
	 */
	@RequestMapping("/deleteVideoInfoByVideoId")
	public ResponseVO deleteVideoInfoByVideoId(String videoId) {
		videoInfoService.deleteVideoInfoByVideoId(videoId);
		return getSuccessResponseVO(null);
	}
}