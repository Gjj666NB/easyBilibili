package com.easylive.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.easylive.constants.Constants;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.VideoDanmuQuery;
import com.easylive.entity.po.VideoDanmu;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.service.VideoDanmuService;
import com.easylive.service.VideoInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 视频弹幕 Controller
 */
@RestController("videoDanmuController")
@RequestMapping("/interact/danmu")
public class VideoDanmuController extends ABaseController{

	@Resource
	private VideoDanmuService videoDanmuService;

	@Resource
	private VideoInfoService videoInfoService;

	@RequestMapping("/postDanmu")
	public ResponseVO postDanmu(@NotEmpty String videoId, @NotEmpty String fileId,
								@NotEmpty @Size(max = 200) String text, @NotEmpty String color,
								@NotEmpty Integer mode, @NotNull Integer time) {
		VideoDanmu videoDanmu = new VideoDanmu();
		videoDanmu.setVideoId(videoId);
		videoDanmu.setFileId(fileId);
		videoDanmu.setText(text);
		videoDanmu.setMode(mode);
		videoDanmu.setColor(color);
		videoDanmu.setTime(time);
		videoDanmu.setUserId(getTokenUserInfo().getUserId());
		videoDanmu.setPostTime(new Date());
		videoDanmuService.savaDanmu(videoDanmu);
		return getSuccessResponseVO( null);
	}

	@RequestMapping("/loadDanmu")
	public ResponseVO loadDanmu(@NotEmpty String videoId, @NotEmpty String fileId) {
		VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
		if (videoInfo.getIntroduction() != null && videoInfo.getIntroduction().equals(Constants.ONE.toString())){
			return getSuccessResponseVO(new ArrayList<>());
		}
		VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
		videoDanmuQuery.setFileId(fileId);
		videoDanmuQuery.setOrderBy("v.danmu_id asc");
		return getSuccessResponseVO(videoDanmuService.findListByParam(videoDanmuQuery));
	}
}