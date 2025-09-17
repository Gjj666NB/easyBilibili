package com.easylive.web.controller;

import java.util.List;

import com.easylive.entity.query.VideoInfoFileQuery;
import com.easylive.entity.po.VideoInfoFile;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.VideoInfoFileService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 视频文件信息 Controller
 */
@RestController("videoInfoFileController")
@RequestMapping("/videoInfoFile")
public class VideoInfoFileController extends ABaseController{

	@Resource
	private VideoInfoFileService videoInfoFileService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(VideoInfoFileQuery query){
		return getSuccessResponseVO(videoInfoFileService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(VideoInfoFile bean) {
		videoInfoFileService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<VideoInfoFile> listBean) {
		videoInfoFileService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<VideoInfoFile> listBean) {
		videoInfoFileService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileId查询对象
	 */
	@RequestMapping("/getVideoInfoFileByFileId")
	public ResponseVO getVideoInfoFileByFileId(String fileId) {
		return getSuccessResponseVO(videoInfoFileService.getVideoInfoFileByFileId(fileId));
	}

	/**
	 * 根据FileId修改对象
	 */
	@RequestMapping("/updateVideoInfoFileByFileId")
	public ResponseVO updateVideoInfoFileByFileId(VideoInfoFile bean,String fileId) {
		videoInfoFileService.updateVideoInfoFileByFileId(bean,fileId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据FileId删除
	 */
	@RequestMapping("/deleteVideoInfoFileByFileId")
	public ResponseVO deleteVideoInfoFileByFileId(String fileId) {
		videoInfoFileService.deleteVideoInfoFileByFileId(fileId);
		return getSuccessResponseVO(null);
	}
}