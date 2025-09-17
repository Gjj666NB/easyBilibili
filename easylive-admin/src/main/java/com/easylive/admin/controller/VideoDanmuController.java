package com.easylive.admin.controller;

import java.util.List;

import com.easylive.entity.query.VideoDanmuQuery;
import com.easylive.entity.po.VideoDanmu;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoDanmuService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频弹幕 Controller
 */
@RestController("videoDanmuController")
@RequestMapping("/videoDanmu")
public class VideoDanmuController extends ABaseController{

	@Resource
	private VideoDanmuService videoDanmuService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(VideoDanmuQuery query){
		return getSuccessResponseVO(videoDanmuService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(VideoDanmu bean) {
		videoDanmuService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<VideoDanmu> listBean) {
		videoDanmuService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<VideoDanmu> listBean) {
		videoDanmuService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据DanmuId查询对象
	 */
	@RequestMapping("/getVideoDanmuByDanmuId")
	public ResponseVO getVideoDanmuByDanmuId(Integer danmuId) {
		return getSuccessResponseVO(videoDanmuService.getVideoDanmuByDanmuId(danmuId));
	}

	/**
	 * 根据DanmuId修改对象
	 */
	@RequestMapping("/updateVideoDanmuByDanmuId")
	public ResponseVO updateVideoDanmuByDanmuId(VideoDanmu bean,Integer danmuId) {
		videoDanmuService.updateVideoDanmuByDanmuId(bean,danmuId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据DanmuId删除
	 */
	@RequestMapping("/deleteVideoDanmuByDanmuId")
	public ResponseVO deleteVideoDanmuByDanmuId(Integer danmuId) {
		videoDanmuService.deleteVideoDanmuByDanmuId(danmuId);
		return getSuccessResponseVO(null);
	}
}