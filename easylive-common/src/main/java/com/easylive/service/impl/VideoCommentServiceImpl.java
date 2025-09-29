package com.easylive.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import com.easylive.constants.Constants;
import com.easylive.entity.po.UserInfo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserInfoQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.enums.CommentTopTypeEnum;
import com.easylive.enums.ResponseEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.UserInfoMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.VideoInfoService;
import org.springframework.stereotype.Service;

import com.easylive.enums.PageSize;
import com.easylive.entity.query.VideoCommentQuery;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.query.SimplePage;
import com.easylive.mappers.VideoCommentMapper;
import com.easylive.service.VideoCommentService;
import com.easylive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 评论 业务接口实现
 */
@Service("videoCommentService")
public class VideoCommentServiceImpl implements VideoCommentService {

	@Resource
	private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;


	/**
	 * 根据条件查询列表
	 */
	@Override
		public List<VideoComment> findListByParam(VideoCommentQuery param) {
			if (param.getLoadChildren() != null && param.getLoadChildren()){
				return videoCommentMapper.selectListWithChildren(param);
			}
			return this.videoCommentMapper.selectList(param);
		}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(VideoCommentQuery param) {
		return this.videoCommentMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<VideoComment> findListByPage(VideoCommentQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<VideoComment> list = this.findListByParam(param);
		PaginationResultVO<VideoComment> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(VideoComment bean) {
		return this.videoCommentMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<VideoComment> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoCommentMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<VideoComment> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoCommentMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(VideoComment bean, VideoCommentQuery param) {
		StringTools.checkParam(param);
		return this.videoCommentMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(VideoCommentQuery param) {
		StringTools.checkParam(param);
		return this.videoCommentMapper.deleteByParam(param);
	}

	/**
	 * 根据CommentId获取对象
	 */
	@Override
	public VideoComment getVideoCommentByCommentId(Integer commentId) {
		return this.videoCommentMapper.selectByCommentId(commentId);
	}

	/**
	 * 根据CommentId修改
	 */
	@Override
	public Integer updateVideoCommentByCommentId(VideoComment bean, Integer commentId) {
		return this.videoCommentMapper.updateByCommentId(bean, commentId);
	}

	/**
	 * 根据CommentId删除
	 */
	@Override
	public Integer deleteVideoCommentByCommentId(Integer commentId) {
		return this.videoCommentMapper.deleteByCommentId(commentId);
	}

	@Override
	public void postComment(VideoComment videoComment, Integer replyCommentId) {
		//校验视频存不存在
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoComment.getVideoId());
		if (videoInfo == null){
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		//校验是否开启评论功能
		if (videoInfo.getInteraction()!= null && videoInfo.getInteraction().contains(Constants.ZERO.toString())){
			throw new BusinessException("UP主已关闭评论功能");
		}

		//评论/回复
		if (replyCommentId != null){
			VideoComment replyComment = videoCommentMapper.selectByCommentId(replyCommentId);
			if (replyComment == null || !replyComment.getVideoId().equals(videoComment.getVideoId())){
				throw new BusinessException(ResponseEnum.CODE_600);
			}
			//查看回复评论的层级
			if (replyComment.getpCommentId() == 0){
				//一级评论
				videoComment.setpCommentId(replyComment.getCommentId());
			}else {
				//子评论
				videoComment.setpCommentId(replyComment.getpCommentId());
				videoComment.setReplyUserId(replyComment.getUserId());
			}
			UserInfo replyUserInfo = userInfoMapper.selectByUserId(replyComment.getUserId());
			videoComment.setReplyUserId(replyUserInfo.getUserId());
			videoComment.setReplyNickName(replyUserInfo.getNickName());
			videoComment.setReplyAvatar(replyUserInfo.getAvatar());
		}else {
			videoComment.setpCommentId(0);
		}
		videoComment.setPostTime(new Date());
		videoComment.setVideoUserId(videoInfo.getUserId());
		videoCommentMapper.insert(videoComment);
		if (videoComment.getpCommentId() == 0) {//如果是一级评论，则更新视频的评论数
			videoInfoMapper.updateCountInfo(videoComment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), Constants.ONE);
		}
	}

	@Override
	public void top(Integer commentId, String userId) {
		//取消已被置顶的评论
		VideoComment videoComment = new VideoComment();
		videoComment.setTopType(CommentTopTypeEnum.TOP.getType());
		videoCommentMapper.updateByCommentId(videoComment, commentId);
	}

	@Override
	public void cancelTop(Integer commentId, String userId) {
		VideoComment videoComment = videoCommentMapper.selectByCommentId(commentId);
		//评论是否存在
		if (videoComment == null){
			throw new BusinessException(ResponseEnum.CODE_600);
		}

		if (!videoComment.getTopType().equals(CommentTopTypeEnum.TOP) ){
			throw new BusinessException(ResponseEnum.CODE_600);
		}

		//视频是否存在 是否是作者操作
		UserInfo userInfo = userInfoMapper.selectByUserId(userId);
		if (userInfo == null){
			throw new BusinessException(ResponseEnum.CODE_600);
		}

		if (userInfo.getUserId() != userId){
			throw new BusinessException(ResponseEnum.CODE_600);
		}

		VideoComment comment = new VideoComment();

		comment.setTopType(CommentTopTypeEnum.NO_TOP.getType());

		VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
		videoCommentQuery.setCommentId(commentId);
		videoCommentQuery.setTopType(CommentTopTypeEnum.TOP.getType());
		videoCommentMapper.updateByParam(comment, videoCommentQuery);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delComment(Integer commentId, String userId) {
		VideoComment videoComment = videoCommentMapper.selectByCommentId(commentId);
		if (videoComment == null) {
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoComment.getVideoId());
		if (videoInfo == null) {
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		if (userId != null && !videoInfo.getUserId().equals(userId) && !videoComment.getUserId().equals(userId)) {
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		videoCommentMapper.deleteByCommentId(commentId);


		//如果删除父评论 同时把子评论也删除 并且更改评论数量
		if (videoComment.getpCommentId() == 0){
			videoInfoMapper.updateCountInfo(videoComment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), -Constants.ONE);
			VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
			videoCommentQuery.setpCommentId(commentId);
			videoCommentMapper.deleteByParam(videoCommentQuery);
		}
	}

    @Override
	@Transactional(rollbackFor = Exception.class)
    public void deleteComment(Integer commentId, String userId) {
		VideoComment videoComment = videoCommentMapper.selectByCommentId(commentId);
		if (videoComment == null) {
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(videoComment.getVideoId());
		if (videoInfo == null) {
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		if (userId != null && !videoInfo.getUserId().equals(userId) && !videoComment.getUserId().equals(userId)) {
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		videoCommentMapper.deleteByCommentId(commentId);
		if (videoComment.getpCommentId() == 0) {//如果是一级评论，同时删除该评论下的子评论，并更新视频的评论数
			videoInfoMapper.updateCountInfo(videoComment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), -Constants.ONE);
			VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
			videoCommentQuery.setpCommentId(commentId);
			videoCommentMapper.deleteByParam(videoCommentQuery);
		}
    }


}