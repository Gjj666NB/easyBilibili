package com.easylive.admin.controller;

import java.util.List;

import com.easylive.entity.query.UserActionQuery;
import com.easylive.entity.po.UserAction;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.UserActionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户行为 点赞、评论 Controller
 */
@RestController("userActionController")
@RequestMapping("/userAction")
public class UserActionController extends ABaseController{

	@Resource
	private UserActionService userActionService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserActionQuery query){
		return getSuccessResponseVO(userActionService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserAction bean) {
		userActionService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserAction> listBean) {
		userActionService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserAction> listBean) {
		userActionService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ActionId查询对象
	 */
	@RequestMapping("/getUserActionByActionId")
	public ResponseVO getUserActionByActionId(Integer actionId) {
		return getSuccessResponseVO(userActionService.getUserActionByActionId(actionId));
	}

	/**
	 * 根据ActionId修改对象
	 */
	@RequestMapping("/updateUserActionByActionId")
	public ResponseVO updateUserActionByActionId(UserAction bean,Integer actionId) {
		userActionService.updateUserActionByActionId(bean,actionId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据ActionId删除
	 */
	@RequestMapping("/deleteUserActionByActionId")
	public ResponseVO deleteUserActionByActionId(Integer actionId) {
		userActionService.deleteUserActionByActionId(actionId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据VideoIdAndCommentIdAndActionTypeAndUserId查询对象
	 */
	@RequestMapping("/getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId")
	public ResponseVO getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId,Integer commentId,Integer actionType,String userId) {
		return getSuccessResponseVO(userActionService.getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(videoId,commentId,actionType,userId));
	}

	/**
	 * 根据VideoIdAndCommentIdAndActionTypeAndUserId修改对象
	 */
	@RequestMapping("/updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId")
	public ResponseVO updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(UserAction bean,String videoId,Integer commentId,Integer actionType,String userId) {
		userActionService.updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(bean,videoId,commentId,actionType,userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据VideoIdAndCommentIdAndActionTypeAndUserId删除
	 */
	@RequestMapping("/deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId")
	public ResponseVO deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId,Integer commentId,Integer actionType,String userId) {
		userActionService.deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(videoId,commentId,actionType,userId);
		return getSuccessResponseVO(null);
	}
}