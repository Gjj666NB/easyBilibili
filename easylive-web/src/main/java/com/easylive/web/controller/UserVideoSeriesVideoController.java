package com.easylive.web.controller;

import java.util.List;

import com.easylive.entity.query.UserVideoSeriesVideoQuery;
import com.easylive.entity.po.UserVideoSeriesVideo;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.UserVideoSeriesVideoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *  Controller
 */
@RestController("userVideoSeriesVideoController")
@RequestMapping("/userVideoSeriesVideo")
public class UserVideoSeriesVideoController extends ABaseController{

	@Resource
	private UserVideoSeriesVideoService userVideoSeriesVideoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserVideoSeriesVideoQuery query){
		return getSuccessResponseVO(userVideoSeriesVideoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserVideoSeriesVideo bean) {
		userVideoSeriesVideoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserVideoSeriesVideo> listBean) {
		userVideoSeriesVideoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserVideoSeriesVideo> listBean) {
		userVideoSeriesVideoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SeriesIdAndVideoId查询对象
	 */
	@RequestMapping("/getUserVideoSeriesVideoBySeriesIdAndVideoId")
	public ResponseVO getUserVideoSeriesVideoBySeriesIdAndVideoId(Integer seriesId,String videoId) {
		return getSuccessResponseVO(userVideoSeriesVideoService.getUserVideoSeriesVideoBySeriesIdAndVideoId(seriesId,videoId));
	}

	/**
	 * 根据SeriesIdAndVideoId修改对象
	 */
	@RequestMapping("/updateUserVideoSeriesVideoBySeriesIdAndVideoId")
	public ResponseVO updateUserVideoSeriesVideoBySeriesIdAndVideoId(UserVideoSeriesVideo bean,Integer seriesId,String videoId) {
		userVideoSeriesVideoService.updateUserVideoSeriesVideoBySeriesIdAndVideoId(bean,seriesId,videoId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据SeriesIdAndVideoId删除
	 */
	@RequestMapping("/deleteUserVideoSeriesVideoBySeriesIdAndVideoId")
	public ResponseVO deleteUserVideoSeriesVideoBySeriesIdAndVideoId(Integer seriesId,String videoId) {
		userVideoSeriesVideoService.deleteUserVideoSeriesVideoBySeriesIdAndVideoId(seriesId,videoId);
		return getSuccessResponseVO(null);
	}
}