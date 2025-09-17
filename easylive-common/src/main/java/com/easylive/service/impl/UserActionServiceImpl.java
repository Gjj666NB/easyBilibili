package com.easylive.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.easylive.constants.Constants;
import com.easylive.entity.po.UserInfo;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.*;
import com.easylive.enums.ResponseEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.UserInfoMapper;
import com.easylive.mappers.VideoCommentMapper;
import com.easylive.mappers.VideoInfoMapper;
import org.springframework.stereotype.Service;

import com.easylive.enums.PageSize;
import com.easylive.entity.po.UserAction;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.mappers.UserActionMapper;
import com.easylive.service.UserActionService;
import com.easylive.utils.StringTools;


/**
 * 用户行为 点赞、评论 业务接口实现
 */
@Service("userActionService")
public class UserActionServiceImpl implements UserActionService {

	@Resource
	private UserActionMapper<UserAction, UserActionQuery> userActionMapper;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserAction> findListByParam(UserActionQuery param) {
		return this.userActionMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserActionQuery param) {
		return this.userActionMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserAction> findListByPage(UserActionQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserAction> list = this.findListByParam(param);
		PaginationResultVO<UserAction> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserAction bean) {
		return this.userActionMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserAction> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userActionMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserAction> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userActionMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserAction bean, UserActionQuery param) {
		StringTools.checkParam(param);
		return this.userActionMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserActionQuery param) {
		StringTools.checkParam(param);
		return this.userActionMapper.deleteByParam(param);
	}

	/**
	 * 根据ActionId获取对象
	 */
	@Override
	public UserAction getUserActionByActionId(Integer actionId) {
		return this.userActionMapper.selectByActionId(actionId);
	}

	/**
	 * 根据ActionId修改
	 */
	@Override
	public Integer updateUserActionByActionId(UserAction bean, Integer actionId) {
		return this.userActionMapper.updateByActionId(bean, actionId);
	}

	/**
	 * 根据ActionId删除
	 */
	@Override
	public Integer deleteUserActionByActionId(Integer actionId) {
		return this.userActionMapper.deleteByActionId(actionId);
	}

	/**
	 * 根据VideoIdAndCommentIdAndActionTypeAndUserId获取对象
	 */
	@Override
	public UserAction getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId, Integer commentId, Integer actionType, String userId) {
		return this.userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(videoId, commentId, actionType, userId);
	}

	/**
	 * 根据VideoIdAndCommentIdAndActionTypeAndUserId修改
	 */
	@Override
	public Integer updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(UserAction bean, String videoId, Integer commentId, Integer actionType, String userId) {
		return this.userActionMapper.updateByVideoIdAndCommentIdAndActionTypeAndUserId(bean, videoId, commentId, actionType, userId);
	}

	/**
	 * 根据VideoIdAndCommentIdAndActionTypeAndUserId删除
	 */
	@Override
	public Integer deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId, Integer commentId, Integer actionType, String userId) {
		return this.userActionMapper.deleteByVideoIdAndCommentIdAndActionTypeAndUserId(videoId, commentId, actionType, userId);
	}

	@Override
	public void saveAction(UserAction userAction) {
		VideoInfo videoInfo = videoInfoMapper.selectByVideoId(userAction.getVideoId());
		if (videoInfo == null){
			throw new BusinessException(ResponseEnum.CODE_600);
		}

		userAction.setVideoUserId(videoInfo.getUserId());

		UserActionTypeEnum actionType = UserActionTypeEnum.getByType(userAction.getActionType());
		if (actionType == null){
			throw new BusinessException(ResponseEnum.CODE_600);
		}

		userAction.setActionTime(new Date());
		UserAction dbUserAction = userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(userAction.getVideoId(), userAction.getCommentId(), userAction.getActionType(), userAction.getUserId());

		switch (actionType){
			case VIDEO_LIKE:
			case VIDEO_COLLECT:
				if (dbUserAction != null){
					userActionMapper.deleteByActionId(dbUserAction.getActionId());
				}else {
					userActionMapper.insert(userAction);
				}
				Integer changeCount = dbUserAction == null ? Constants.ONE : -Constants.ONE;
				videoInfoMapper.updateCountInfo(userAction.getVideoId(), actionType.getField(), changeCount);
				if (actionType == UserActionTypeEnum.VIDEO_COLLECT) {
					//更新es收藏数量
					//esSearchComponent.updateDocCount(userAction.getVideoId(), SearchOrderTypeEnum.VIDEO_COLLECT.getField(), changeCount);
				}
				break;
			case VIDEO_COIN:
				if (videoInfo.getUserId().equals(userAction.getUserId())){
					throw new BusinessException("UP🐷不能给自己投币");
				}
				if (dbUserAction != null){
					throw new BusinessException("已经投过币了");
				}
				//修改硬币数量
				Integer updateCoinCount = userInfoMapper.updateCoinCount(userAction.getUserId(), -userAction.getActionCount());
				if (updateCoinCount == 0){
					throw new BusinessException("你的硬币不足");
				}

				updateCoinCount = userInfoMapper.updateCoinCount(videoInfo.getUserId(), userAction.getActionCount());
				if (updateCoinCount == 0){
					throw new BusinessException("投币失败");
				}

				userActionMapper.insert(userAction);

				videoInfoMapper.updateCountInfo(userAction.getVideoId(), actionType.getField(), userAction.getActionCount());
				break;
			case COMMENT_LIKE:
			case COMMENT_HATE:
				UserActionTypeEnum opposeType = actionType.equals(UserActionTypeEnum.COMMENT_LIKE) ? UserActionTypeEnum.COMMENT_HATE : UserActionTypeEnum.COMMENT_LIKE;
				UserAction opposeAction = userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(userAction.getVideoId(), userAction.getCommentId(), opposeType.getType(), userAction.getUserId());
				if (opposeAction != null){
					userActionMapper.deleteByActionId(opposeAction.getActionId());
				}

				if (dbUserAction != null){
					userActionMapper.deleteByActionId(dbUserAction.getActionId());
				}else {
					userActionMapper.insert(userAction);
				}

				changeCount = dbUserAction == null ? Constants.ONE : -Constants.ONE;
				Integer opposeChangeCount = - changeCount;
				videoCommentMapper.updateCountInfo(userAction.getCommentId(), actionType.getField(), changeCount,
						opposeAction == null ? null : opposeType.getField(), opposeChangeCount);
				break;
		}
	}
}