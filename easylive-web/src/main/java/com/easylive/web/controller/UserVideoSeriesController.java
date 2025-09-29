package com.easylive.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.UserVideoSeriesVideo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserVideoSeriesQuery;
import com.easylive.entity.po.UserVideoSeries;
import com.easylive.entity.query.UserVideoSeriesVideoQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.entity.vo.UserVideoSeriesDetailVo;
import com.easylive.enums.ResponseEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.UserVideoSeriesVideoMapper;
import com.easylive.service.UserVideoSeriesService;
import com.easylive.service.UserVideoSeriesVideoService;
import com.easylive.service.VideoInfoService;
import com.easylive.web.annotation.GlobalInterceptor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户视频顺序列表档 Controller
 */
@RestController("userVideoSeriesController")
@RequestMapping("/ucenter/series/")
public class UserVideoSeriesController extends ABaseController{

	@Resource
	private UserVideoSeriesService userVideoSeriesService;

	@Resource
	private UserVideoSeriesVideoService userVideoSeriesVideoService;

	@Resource
	private VideoInfoService videoInfoService;

	@RequestMapping("loadVideoSeries")
	public ResponseVO loadVideoSeries( String userId) {
		List<UserVideoSeries> list = userVideoSeriesService.getUserAllVideoSeries(userId);
		return getSuccessResponseVO(list);
	}

	@RequestMapping("saveVideoSeries")
	public ResponseVO saveVideoSeries(Integer seriesId, @NotEmpty @Size(max = 100) String seriesName,
									  @Size(max = 200) String seriesDescription, String videoIds){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		UserVideoSeries userVideoSeries = new UserVideoSeries();
		userVideoSeries.setSeriesId(seriesId);
		userVideoSeries.setSeriesName(seriesName);
		userVideoSeries.setSeriesDescription(seriesDescription);
		userVideoSeries.setUserId(tokenUserInfo.getUserId());
		userVideoSeriesService.saveUserVideoSeries(userVideoSeries,videoIds);
		return getSuccessResponseVO( null);
	}

	@RequestMapping("loadAllVideo")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO loadAllVideo(Integer seriesId){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		if (seriesId != null){
			//过滤已添加视频
			UserVideoSeriesVideoQuery userVideoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
			userVideoSeriesVideoQuery.setSeriesId(seriesId);
			userVideoSeriesVideoQuery.setUserId(tokenUserInfo.getUserId());
			List<UserVideoSeriesVideo> list = userVideoSeriesVideoService.findListByParam(userVideoSeriesVideoQuery);
			List<String> videoIdList = list.stream().map(item -> item.getVideoId()).collect(Collectors.toList());
			videoInfoQuery.setExcludeVideoIdArray(videoIdList.toArray(new String[videoIdList.size()]));
		}
		videoInfoQuery.setUserId(tokenUserInfo.getUserId());
		List<VideoInfo> videoInfoList = videoInfoService.findListByParam(videoInfoQuery);
		return getSuccessResponseVO(videoInfoList);
	}

	@RequestMapping("getVideoSeriesDetail")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO getVideoSeriesDetail(@NotNull Integer seriesId){

		UserVideoSeries userVideoSeriesBySeriesId = userVideoSeriesService.getUserVideoSeriesBySeriesId(seriesId);
		if (userVideoSeriesBySeriesId == null){
			throw new BusinessException(ResponseEnum.CODE_404);
		}

		UserVideoSeriesVideoQuery userVideoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
		userVideoSeriesVideoQuery.setSeriesId(seriesId);
		userVideoSeriesVideoQuery.setQueryVideoInfo(true);
		userVideoSeriesVideoQuery.setOrderBy("u.sort asc");
		List<UserVideoSeriesVideo> list = userVideoSeriesVideoService.findListByParam(userVideoSeriesVideoQuery);

		UserVideoSeriesDetailVo userVideoSeriesDetailVo = new UserVideoSeriesDetailVo();

		userVideoSeriesDetailVo.setUserVideoSeries(userVideoSeriesBySeriesId);
		userVideoSeriesDetailVo.setUserVideoSeriesVideos( list);

		return getSuccessResponseVO(userVideoSeriesDetailVo);
	}


	//添加系列视频
	@RequestMapping("/saveSeriesVideo")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO saveSeriesVideo(@NotNull Integer seriesId,
									  @NotEmpty String videoIds) {
		TokenUserInfoDto userInfoDto = getTokenUserInfo();
		userVideoSeriesService.saveVideo2UserVideoSeries(userInfoDto.getUserId(), videoIds, seriesId);
		return getSuccessResponseVO(null);
	}


	@RequestMapping("/delSeriesVideo")
	public ResponseVO delSeriesVideo(@NotNull Integer seriesId,
									 @NotEmpty String videoId){
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		userVideoSeriesService.delSeriesVideo(tokenUserInfo.getUserId(), videoId,seriesId);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/loadVideoSeriesWithVideo")
	public ResponseVO loadVideoSeriesWithVideo(@NotEmpty String userId){
		UserVideoSeriesQuery userVideoSeriesQuery = new UserVideoSeriesQuery();
		userVideoSeriesQuery.setUserId(userId);
		userVideoSeriesQuery.setOrderBy("u.sort asc");
		List<UserVideoSeries> userVideoSeriesList = userVideoSeriesService.findListWithVideo(userVideoSeriesQuery);
		return getSuccessResponseVO(userVideoSeriesList);
	}

	/**
	 * 删除系列
	 * @param seriesId
	 * @return
	 */
	@RequestMapping("/delSeries")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO delSeries(@NotNull Integer seriesId) {
		TokenUserInfoDto userInfoDto = getTokenUserInfo();
		userVideoSeriesService.delSeries(userInfoDto.getUserId(), seriesId);
		return getSuccessResponseVO(null);
	}







}