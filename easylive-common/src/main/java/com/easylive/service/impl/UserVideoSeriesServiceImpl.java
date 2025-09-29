package com.easylive.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.easylive.entity.po.UserVideoSeriesVideo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserVideoSeriesVideoQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.UserVideoSeriesVideoMapper;
import com.easylive.mappers.VideoInfoMapper;
import org.springframework.stereotype.Service;


import com.easylive.entity.query.UserVideoSeriesQuery;
import com.easylive.entity.po.UserVideoSeries;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.query.SimplePage;
import com.easylive.mappers.UserVideoSeriesMapper;
import com.easylive.service.UserVideoSeriesService;
import com.easylive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用户视频顺序列表档 业务接口实现
 */
@Service("userVideoSeriesService")
public class UserVideoSeriesServiceImpl implements UserVideoSeriesService {

	@Resource
	private UserVideoSeriesMapper<UserVideoSeries, UserVideoSeriesQuery> userVideoSeriesMapper;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	@Resource
	private UserVideoSeriesVideoMapper<UserVideoSeriesVideo, UserVideoSeriesVideoQuery> userVideoSeriesVideoMapper;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserVideoSeries> findListByParam(UserVideoSeriesQuery param) {
		return this.userVideoSeriesMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserVideoSeriesQuery param) {
		return this.userVideoSeriesMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserVideoSeries> findListByPage(UserVideoSeriesQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserVideoSeries> list = this.findListByParam(param);
		PaginationResultVO<UserVideoSeries> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserVideoSeries bean) {
		return this.userVideoSeriesMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserVideoSeries> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userVideoSeriesMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserVideoSeries> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userVideoSeriesMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserVideoSeries bean, UserVideoSeriesQuery param) {
		StringTools.checkParam(param);
		return this.userVideoSeriesMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserVideoSeriesQuery param) {
		StringTools.checkParam(param);
		return this.userVideoSeriesMapper.deleteByParam(param);
	}

	/**
	 * 根据SeriesId获取对象
	 */
	@Override
	public UserVideoSeries getUserVideoSeriesBySeriesId(Integer seriesId) {
		return this.userVideoSeriesMapper.selectBySeriesId(seriesId);
	}

	/**
	 * 根据SeriesId修改
	 */
	@Override
	public Integer updateUserVideoSeriesBySeriesId(UserVideoSeries bean, Integer seriesId) {
		return this.userVideoSeriesMapper.updateBySeriesId(bean, seriesId);
	}

	/**
	 * 根据SeriesId删除
	 */
	@Override
	public Integer deleteUserVideoSeriesBySeriesId(Integer seriesId) {
		return this.userVideoSeriesMapper.deleteBySeriesId(seriesId);
	}

    @Override
	@Transactional(rollbackFor = Exception.class)
    public void saveUserVideoSeries(UserVideoSeries userVideoSeries, String videoIds) {
		//新增时没有传入视频合集
		if (userVideoSeries.getSeriesId() == null && StringTools.isEmpty(videoIds)){
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		//新增视频系列
		if (userVideoSeries.getSeriesId() == null){
			checkVideoIds(userVideoSeries.getUserId(),videoIds);
			userVideoSeries.setUpdateTime(new Date());
			userVideoSeries.setSort(userVideoSeriesMapper.selectMaxCount(userVideoSeries.getUserId()) + 1);
			userVideoSeriesMapper.insert(userVideoSeries);
			saveVideo2UserVideoSeries(userVideoSeries.getUserId(), videoIds, userVideoSeries.getSeriesId());
		}else {
			//修改视频系列
			userVideoSeriesMapper.updateBySeriesId(userVideoSeries,userVideoSeries.getSeriesId());
		}
    }

	@Override
	public List<UserVideoSeries> getUserAllVideoSeries(String userId) {
		return userVideoSeriesMapper.selectUserAllVideoSeries(userId);
	}

	public void saveVideo2UserVideoSeries(String userId, String videoIds, Integer seriesId) {
		checkVideoIds(userId,videoIds);
		String[] videoIdArray = videoIds.split(",");
		Integer sort = userVideoSeriesVideoMapper.selectMaxSort(seriesId);
		ArrayList<UserVideoSeriesVideo> list = new ArrayList<>();
		for (String videoId : videoIdArray){
			UserVideoSeriesVideo userVideoSeriesVideo = new UserVideoSeriesVideo();
			userVideoSeriesVideo.setSeriesId(seriesId);
			userVideoSeriesVideo.setVideoId(videoId);
			userVideoSeriesVideo.setSort(++sort);
			userVideoSeriesVideo.setUserId(userId);
			list.add(userVideoSeriesVideo);
		}
		userVideoSeriesVideoMapper.insertBatch(list);

	}

	@Override
	public void delSeriesVideo(String userId, String videoId, Integer seriesId) {
		UserVideoSeriesVideoQuery userVideoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
		userVideoSeriesVideoQuery.setSeriesId(seriesId);
		userVideoSeriesVideoQuery.setVideoId(videoId);
		userVideoSeriesVideoQuery.setUserId(userId);
		Integer count = userVideoSeriesVideoMapper.deleteByParam(userVideoSeriesVideoQuery);

		if (count == 0){
			throw new BusinessException(ResponseEnum.CODE_600);
		}
	}

	@Override
	public List<UserVideoSeries> findListWithVideo(UserVideoSeriesQuery userVideoSeriesQuery) {
		return userVideoSeriesMapper.selectListWithVideo(userVideoSeriesQuery);
	}

	@Override
	public void delSeries(String userId, Integer seriesId) {
		//先删除系列
		UserVideoSeriesQuery userVideoSeriesQuery = new UserVideoSeriesQuery();
		userVideoSeriesQuery.setUserId(userId);
		userVideoSeriesQuery.setSeriesId(seriesId);
		Integer count = userVideoSeriesMapper.deleteByParam(userVideoSeriesQuery);
		if (count == 0){
			throw  new BusinessException(ResponseEnum.CODE_600);
		}
		//再删除系列视频
		UserVideoSeriesVideoQuery userVideoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
		userVideoSeriesVideoQuery.setSeriesId(seriesId);
		userVideoSeriesVideoQuery.setUserId(userId);
		userVideoSeriesVideoMapper.deleteByParam(userVideoSeriesVideoQuery);
	}

	private void checkVideoIds(String userId, String videoIds) {
		String[] videoIdArray = videoIds.split(",");
		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		videoInfoQuery.setUserId(userId);
		videoInfoQuery.setVideoIdArray(videoIdArray);
		Integer count = videoInfoMapper.selectCount(videoInfoQuery);
		if (count != videoIdArray.length){
			throw new BusinessException(ResponseEnum.CODE_600);
		}
	}
}