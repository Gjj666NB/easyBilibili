package com.easylive.web.controller;

import java.util.List;

import com.easylive.entity.query.VideoInfoPostQuery;
import com.easylive.entity.po.VideoInfoPost;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoInfoPostService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频信息 Controller
 */
@RestController("videoInfoPostController")
@RequestMapping("/videoInfoPost")
public class VideoInfoPostController extends ABaseController{

	@Resource
	private VideoInfoPostService videoInfoPostService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(VideoInfoPostQuery query){
		return getSuccessResponseVO(videoInfoPostService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(VideoInfoPost bean) {
		videoInfoPostService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<VideoInfoPost> listBean) {
		videoInfoPostService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<VideoInfoPost> listBean) {
		videoInfoPostService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据VideoId查询对象
	 */
	@RequestMapping("/getVideoInfoPostByVideoId")
	public ResponseVO getVideoInfoPostByVideoId(String videoId) {
		return getSuccessResponseVO(videoInfoPostService.getVideoInfoPostByVideoId(videoId));
	}

	/**
	 * 根据VideoId修改对象
	 */
	@RequestMapping("/updateVideoInfoPostByVideoId")
	public ResponseVO updateVideoInfoPostByVideoId(VideoInfoPost bean,String videoId) {
		videoInfoPostService.updateVideoInfoPostByVideoId(bean,videoId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据VideoId删除
	 */
	@RequestMapping("/deleteVideoInfoPostByVideoId")
	public ResponseVO deleteVideoInfoPostByVideoId(String videoId) {
		videoInfoPostService.deleteVideoInfoPostByVideoId(videoId);
		return getSuccessResponseVO(null);
	}
}