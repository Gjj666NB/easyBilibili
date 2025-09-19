package com.easylive.web.controller;

import java.util.List;

import com.easylive.entity.query.UserVideoSeriesQuery;
import com.easylive.entity.po.UserVideoSeries;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.UserVideoSeriesService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户视频顺序列表档 Controller
 */
@RestController("userVideoSeriesController")
@RequestMapping("/userVideoSeries")
public class UserVideoSeriesController extends ABaseController{

	@Resource
	private UserVideoSeriesService userVideoSeriesService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserVideoSeriesQuery query){
		return getSuccessResponseVO(userVideoSeriesService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserVideoSeries bean) {
		userVideoSeriesService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserVideoSeries> listBean) {
		userVideoSeriesService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserVideoSeries> listBean) {
		userVideoSeriesService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SeriesId查询对象
	 */
	@RequestMapping("/getUserVideoSeriesBySeriesId")
	public ResponseVO getUserVideoSeriesBySeriesId(Integer seriesId) {
		return getSuccessResponseVO(userVideoSeriesService.getUserVideoSeriesBySeriesId(seriesId));
	}

	/**
	 * 根据SeriesId修改对象
	 */
	@RequestMapping("/updateUserVideoSeriesBySeriesId")
	public ResponseVO updateUserVideoSeriesBySeriesId(UserVideoSeries bean,Integer seriesId) {
		userVideoSeriesService.updateUserVideoSeriesBySeriesId(bean,seriesId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SeriesId删除
	 */
	@RequestMapping("/deleteUserVideoSeriesBySeriesId")
	public ResponseVO deleteUserVideoSeriesBySeriesId(Integer seriesId) {
		userVideoSeriesService.deleteUserVideoSeriesBySeriesId(seriesId);
		return getSuccessResponseVO(null);
	}
}