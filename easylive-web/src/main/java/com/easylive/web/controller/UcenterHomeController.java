package com.easylive.web.controller;

import com.easylive.constants.Constants;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.UserFocus;
import com.easylive.entity.po.UserInfo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserFocusQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.entity.vo.UserInfoVo;
import com.easylive.enums.PageSize;
import com.easylive.enums.VideoOrderTypeEnum;
import com.easylive.service.UserActionService;
import com.easylive.service.UserFocusService;
import com.easylive.service.UserInfoService;
import com.easylive.service.VideoInfoService;
import com.easylive.utils.CopyTools;
import com.easylive.web.annotation.GlobalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.validation.constraints.*;
import java.util.List;

/**
 *  Controller
 */
@RestController
@RequestMapping("/ucenter/home")
@Validated
@Slf4j
public class UcenterHomeController extends ABaseController{

	@Resource
	private UserInfoService userInfoService;

	@Resource
	private VideoInfoService videoInfoService;

	@Resource
	private UserActionService userActionService;

	@Resource
	private UserFocusService userFocusService;

	@RequestMapping("/getUserInfo")
	public ResponseVO getUserInfo(@NotEmpty String userId) {
		TokenUserInfoDto userInfoDto = getTokenUserInfo();
		UserInfo userInfo =userInfoService.getUserInfoDetail(userInfoDto == null ? null : userInfoDto.getUserId(), userId);
		UserInfoVo userInfoVo = CopyTools.copy(userInfo, UserInfoVo.class);
		return getSuccessResponseVO(userInfoVo);
	}

	@RequestMapping("/updateUserInfo")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO updateUserInfo(@NotEmpty @Size(max = 100) String avatar,
									 @NotEmpty @Size(max = 20) String nickName,
									 @NotNull Integer sex,
									 @Size(max = 100) String birthday,
									 @Size(max = 100) String school,
									 @Size(max = 300) String noticeInfo,
									 @Size(max = 80) String personalIntroduction) {

		TokenUserInfoDto userInfoDto = getTokenUserInfo();
		UserInfo userInfo = new UserInfo();
		userInfo.setAvatar(avatar);
		userInfo.setNickName(nickName);
		userInfo.setSex(sex);
		userInfo.setBirthday(birthday);
		userInfo.setSchool(school);
		userInfo.setpersonIntroduction(personalIntroduction);

		userInfoService.updateUserInfo(userInfo,userInfoDto);
		return getSuccessResponseVO(null);
	}

	@RequestMapping ("/loadVideoList")
	public ResponseVO loadVideoList(@NotEmpty String userId, Integer type,
									Integer pageNo, String videoName, Integer orderType){
		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		if (type != null) {
			videoInfoQuery.setPageSize(PageSize.SIZE15.getSize());
		}
		VideoOrderTypeEnum videoOrderTypeEnum = VideoOrderTypeEnum.getByType(orderType);
		if (videoOrderTypeEnum == null) {
			videoOrderTypeEnum = VideoOrderTypeEnum.CREATE_TIME;
		}
		videoInfoQuery.setOrderBy(videoOrderTypeEnum.getField() + " desc");
		videoInfoQuery.setVideoNameFuzzy(videoName);
		videoInfoQuery.setUserId(userId);
		videoInfoQuery.setPageNo(pageNo);

		PaginationResultVO<VideoInfo> paginationResultVo = videoInfoService.findListByPage(videoInfoQuery);
		return getSuccessResponseVO(paginationResultVo);
	}

	@RequestMapping ("/saveTheme")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO saveTheme(@Min(1) @Max(10) @NotNull Integer theme){
		TokenUserInfoDto userInfoDto = getTokenUserInfo();
		UserInfo userInfo = new UserInfo();
		userInfo.setTheme(theme);
		userInfoService.updateUserInfoByUserId(userInfo,userInfoDto.getUserId());
		return getSuccessResponseVO(null);
	}

	@RequestMapping ("/focus")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO focus(@NotEmpty String focusUserId){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		userFocusService.focus(tokenUserInfo.getUserId(), focusUserId);
		return getSuccessResponseVO(null);
	}


	@RequestMapping ("/cancelFocus")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO cancelFocus(@NotEmpty String focusUserId){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		userFocusService.cancelFocus(tokenUserInfo.getUserId(), focusUserId);
		return getSuccessResponseVO(null);
	}

	@RequestMapping ("/loadFocusList")
	public ResponseVO loadFocusList(Integer pageNo){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		UserFocusQuery userFocusQuery = new UserFocusQuery();
		userFocusQuery.setUserId(tokenUserInfo.getUserId());
		userFocusQuery.setPageNo(pageNo);
		userFocusQuery.setQueryType(Constants.ZERO);
		userFocusQuery.setOrderBy("u.focus_time desc");
		PaginationResultVO<UserFocus> paginationResultVO = userFocusService.findListByPage(userFocusQuery);
		return getSuccessResponseVO(paginationResultVO);
	}

	@RequestMapping ("/loadFansList")
	public ResponseVO loadFansList(Integer pageNo){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		UserFocusQuery userFocusQuery = new UserFocusQuery();
		userFocusQuery.setFocusUserId(tokenUserInfo.getUserId());
		userFocusQuery.setPageNo(pageNo);
		userFocusQuery.setQueryType(Constants.ONE);
		userFocusQuery.setOrderBy("u.focus_time desc");
		PaginationResultVO<UserFocus> paginationResultVO = userFocusService.findListByPage(userFocusQuery);
		return getSuccessResponseVO(paginationResultVO);
	}





}