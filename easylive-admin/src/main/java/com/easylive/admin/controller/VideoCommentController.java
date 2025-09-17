package com.easylive.admin.controller;

import java.util.List;

import com.easylive.entity.query.VideoCommentQuery;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoCommentService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 评论 Controller
 */
@RestController("videoCommentController")
@RequestMapping("/videoComment")
public class VideoCommentController extends ABaseController{

	@Resource
	private VideoCommentService videoCommentService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(VideoCommentQuery query){
		return getSuccessResponseVO(videoCommentService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(VideoComment bean) {
		videoCommentService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<VideoComment> listBean) {
		videoCommentService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<VideoComment> listBean) {
		videoCommentService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CommentId查询对象
	 */
	@RequestMapping("/getVideoCommentByCommentId")
	public ResponseVO getVideoCommentByCommentId(Integer commentId) {
		return getSuccessResponseVO(videoCommentService.getVideoCommentByCommentId(commentId));
	}

	/**
	 * 根据CommentId修改对象
	 */
	@RequestMapping("/updateVideoCommentByCommentId")
	public ResponseVO updateVideoCommentByCommentId(VideoComment bean,Integer commentId) {
		videoCommentService.updateVideoCommentByCommentId(bean,commentId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据CommentId删除
	 */
	@RequestMapping("/deleteVideoCommentByCommentId")
	public ResponseVO deleteVideoCommentByCommentId(Integer commentId) {
		videoCommentService.deleteVideoCommentByCommentId(commentId);
		return getSuccessResponseVO(null);
	}
}