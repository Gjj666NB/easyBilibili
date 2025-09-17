package com.easylive.admin.controller;

import java.util.List;

import com.easylive.entity.query.VideoInfoFilePostQuery;
import com.easylive.entity.po.VideoInfoFilePost;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoInfoFilePostService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频文件信息 Controller
 */
@RestController("videoInfoFilePostController")
@RequestMapping("/videoInfoFilePost")
public class VideoInfoFilePostController extends ABaseController{

	@Resource
	private VideoInfoFilePostService videoInfoFilePostService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(VideoInfoFilePostQuery query){
		return getSuccessResponseVO(videoInfoFilePostService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(VideoInfoFilePost bean) {
		videoInfoFilePostService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<VideoInfoFilePost> listBean) {
		videoInfoFilePostService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<VideoInfoFilePost> listBean) {
		videoInfoFilePostService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileId查询对象
	 */
	@RequestMapping("/getVideoInfoFilePostByFileId")
	public ResponseVO getVideoInfoFilePostByFileId(String fileId) {
		return getSuccessResponseVO(videoInfoFilePostService.getVideoInfoFilePostByFileId(fileId));
	}

	/**
	 * 根据FileId修改对象
	 */
	@RequestMapping("/updateVideoInfoFilePostByFileId")
	public ResponseVO updateVideoInfoFilePostByFileId(VideoInfoFilePost bean,String fileId) {
		videoInfoFilePostService.updateVideoInfoFilePostByFileId(bean,fileId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileId删除
	 */
	@RequestMapping("/deleteVideoInfoFilePostByFileId")
	public ResponseVO deleteVideoInfoFilePostByFileId(String fileId) {
		videoInfoFilePostService.deleteVideoInfoFilePostByFileId(fileId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UploadIdAndUserId查询对象
	 */
	@RequestMapping("/getVideoInfoFilePostByUploadIdAndUserId")
	public ResponseVO getVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId) {
		return getSuccessResponseVO(videoInfoFilePostService.getVideoInfoFilePostByUploadIdAndUserId(uploadId,userId));
	}

	/**
	 * 根据UploadIdAndUserId修改对象
	 */
	@RequestMapping("/updateVideoInfoFilePostByUploadIdAndUserId")
	public ResponseVO updateVideoInfoFilePostByUploadIdAndUserId(VideoInfoFilePost bean,String uploadId,String userId) {
		videoInfoFilePostService.updateVideoInfoFilePostByUploadIdAndUserId(bean,uploadId,userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UploadIdAndUserId删除
	 */
	@RequestMapping("/deleteVideoInfoFilePostByUploadIdAndUserId")
	public ResponseVO deleteVideoInfoFilePostByUploadIdAndUserId(String uploadId,String userId) {
		videoInfoFilePostService.deleteVideoInfoFilePostByUploadIdAndUserId(uploadId,userId);
		return getSuccessResponseVO(null);
	}
}