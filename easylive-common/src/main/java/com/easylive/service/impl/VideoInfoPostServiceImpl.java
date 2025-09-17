package com.easylive.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.easylive.component.RedisComponent;
import com.easylive.config.AppConfig;
import com.easylive.constants.Constants;
import com.easylive.entity.dto.SysSettingDto;
import com.easylive.entity.dto.UploadingFileDto;
import com.easylive.enums.VideoFileTransferResultEnum;
import com.easylive.enums.VideoFileUpdateTypeEnum;
import com.easylive.entity.po.*;
import com.easylive.entity.query.*;
import com.easylive.enums.ResponseEnum;
import com.easylive.enums.VideoStatusEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.*;
import com.easylive.utils.CopyTools;
import com.easylive.utils.FFmpegUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.easylive.enums.PageSize;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.service.VideoInfoPostService;
import com.easylive.utils.StringTools;
import org.springframework.transaction.annotation.Transactional;


/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoPostService")
public class VideoInfoPostServiceImpl implements VideoInfoPostService {

	private static final Logger log = LoggerFactory.getLogger(VideoInfoPostServiceImpl.class);

	@Resource
	private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;

	@Resource
	private AppConfig appConfig;

	@Resource
	private FFmpegUtils ffmpegUtils;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private VideoInfoFileMapper<VideoInfoFile, VideoInfoFileQuery> videoInfoFileMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<VideoInfoPost> findListByParam(VideoInfoPostQuery param) {
		return this.videoInfoPostMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(VideoInfoPostQuery param) {
		return this.videoInfoPostMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<VideoInfoPost> findListByPage(VideoInfoPostQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<VideoInfoPost> list = this.findListByParam(param);
		PaginationResultVO<VideoInfoPost> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(VideoInfoPost bean) {
		return this.videoInfoPostMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<VideoInfoPost> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoInfoPostMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<VideoInfoPost> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoInfoPostMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(VideoInfoPost bean, VideoInfoPostQuery param) {
		StringTools.checkParam(param);
		return this.videoInfoPostMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(VideoInfoPostQuery param) {
		StringTools.checkParam(param);
		return this.videoInfoPostMapper.deleteByParam(param);
	}

	/**
	 * 根据VideoId获取对象
	 */
	@Override
	public VideoInfoPost getVideoInfoPostByVideoId(String videoId) {
		return this.videoInfoPostMapper.selectByVideoId(videoId);
	}

	/**
	 * 根据VideoId修改
	 */
	@Override
	public Integer updateVideoInfoPostByVideoId(VideoInfoPost bean, String videoId) {
		return this.videoInfoPostMapper.updateByVideoId(bean, videoId);
	}

	/**
	 * 根据VideoId删除
	 */
	@Override
	public Integer deleteVideoInfoPostByVideoId(String videoId) {
		return this.videoInfoPostMapper.deleteByVideoId(videoId);
	}

    @Override
	@Transactional(rollbackFor = Exception.class)
    public void savaVideoInfo(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> uploadFileList) {
		//上传文件限制
		if (uploadFileList.size()> redisComponent.getSysSetting().getVideoCount()){
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		//视频ID
		String videoId = videoInfoPost.getVideoId();

		if (!StringTools.isEmpty(videoId)){
			VideoInfoPost videoInfoPostDb = videoInfoPostMapper.selectByVideoId(videoId);
			if(videoInfoPostDb == null){
				throw new BusinessException(ResponseEnum.CODE_600);
			}
			//转码中 待审核 不允许更新
			if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getStatus(), VideoStatusEnum.STATUS2.getStatus()},
					videoInfoPostDb.getStatus())){
				throw new BusinessException(ResponseEnum.CODE_600);
			}
		}

		Date curDate = new Date();
		List<VideoInfoFilePost> deleteList = new ArrayList<>();
		List<VideoInfoFilePost> addList = uploadFileList;

		//新增操作
		if (StringTools.isEmpty(videoId)){
			videoId = StringTools.getRandomLetters(Constants.LENGTH_10);
			videoInfoPost.setVideoId(videoId);
			videoInfoPost.setCreateTime(curDate);
			videoInfoPost.setLastUpdateTime(curDate);
			videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
			videoInfoPostMapper.insert(videoInfoPost);
		}else {//更新操作
			VideoInfoFilePostQuery videoInfoPostQuery = new VideoInfoFilePostQuery();
			videoInfoPostQuery.setVideoId(videoId);
			videoInfoPostQuery.setUserId(videoInfoPost.getUserId());

			//查询数据库中已经存在的视频信息
			List<VideoInfoFilePost> dbVideoInfoFilePostList = videoInfoFilePostMapper.selectList(videoInfoPostQuery);
			//将请求传来的视频文件信息转换为一uploadId为key的Map
			Map<String, VideoInfoFilePost> uploadFileMap = uploadFileList.stream().
					collect(Collectors.toMap(item -> item.getUploadId(), Function.identity(),
							(date1,date2) -> date2));

			Boolean updateFileName = false;

			//对比数据库中存在的文件和传来的文件
			for (VideoInfoFilePost videoInfoFilePost : dbVideoInfoFilePostList){
				VideoInfoFilePost uploadFile = uploadFileMap.get(videoInfoFilePost.getUploadId());
				//传来的视频文件不存在uploadId 说明此文件被删除
				if (uploadFile != null){
					//数据库中已存在的视频文件信息加入待删除了列表
					deleteList.add(videoInfoFilePost);
					//传来的视频文件存在uploadId,但文件名有变化
				} else if (!uploadFile.getFileName().equals(videoInfoFilePost.getFileName())){
					updateFileName = true;
				}
			}

			//此次更新操作中新增视频文件信息
			addList = uploadFileList.stream().filter(item -> uploadFileMap.get(item.getUploadId()) == null).collect(Collectors.toList());
			videoInfoPost.setLastUpdateTime(curDate);

			//检查此次操作基本信息是否有更新
			Boolean changeVideoInfo = changeVideoInfo(videoInfoPost);

			//新增视频文件信息
			if (addList != null && !addList.isEmpty()){
				videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
			}else if (changeVideoInfo || updateFileName){
				videoInfoPost.setStatus(VideoStatusEnum.STATUS2.getStatus());
			}
			videoInfoPostMapper.updateByVideoId(videoInfoPost, videoId);
		}

		//删除视频信息文件
		if (!deleteList.isEmpty()){
			//删除数据库中的视频文件信息
			List<String> deleteFileIdList = deleteList.stream().map(item -> item.getFileId()).collect(Collectors.toList());
			videoInfoFilePostMapper.deleteBatchByFileId(deleteFileIdList,videoInfoPost.getUserId());
			//删除存储在redis中的文件
			List<String> deleteFilePathList = deleteList.stream().map(item -> item.getFilePath()).collect(Collectors.toList());
			redisComponent.addFile2DelQueue(videoId, deleteFilePathList);
		}

		//对视频文件信息进行处理
		int index = 1;
		for (VideoInfoFilePost videoInfoFilePost : uploadFileList){
			videoInfoFilePost.setVideoId(videoId);
			videoInfoFilePost.setFileIndex(index++);
			videoInfoFilePost.setUserId(videoInfoPost.getUserId());
			if (videoInfoFilePost.getFileId()==null){//新增
				videoInfoFilePost.setFileId(StringTools.getRandomLetters(Constants.LENGTH_20));
				videoInfoFilePost.setUpdateType(VideoFileUpdateTypeEnum.UPDATE.getStatus());
				videoInfoFilePost.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
			}
		}
		videoInfoFilePostMapper.insertOrUpdateBatch(uploadFileList);

		if (addList != null && !addList.isEmpty()){
			for (VideoInfoFilePost videoInfoFilePost : addList){
				videoInfoFilePost.setUserId(videoInfoPost.getUserId());
				videoInfoFilePost.setVideoId(videoId);
			}
			redisComponent.addFile2TransferQueue(addList);
		}
	}

	@Override
	public void transferVideoFile(VideoInfoFilePost videoInfoFilePost) {
		VideoInfoFilePost updateFilePost = new VideoInfoFilePost();
		try {
		UploadingFileDto videoFileInfo = redisComponent.getVideoFileInfo(videoInfoFilePost.getUserId(), videoInfoFilePost.getUploadId());

		//找到存储第一次分片文件的临时目录
		String tempFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + videoFileInfo.getFilePath();
		File tempFile = new File(tempFilePath);
		//最终存储目录
		String targetFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_VIDEO + videoFileInfo.getFilePath();
		File targetFile = new File(targetFilePath);

		if (!tempFile.exists()){
			targetFile.mkdirs();
		}

		FileUtils.copyDirectory(tempFile, targetFile);
		redisComponent.deleteVideoFileInfo(videoInfoFilePost.getUserId(),videoInfoFilePost.getUploadId());

		//合并成mp4文件
		String completeVideo = targetFilePath + Constants.TEMP_VIDEO_NAME;
		union(targetFilePath, completeVideo,true);

		//根据 temp.mp4获取播放时长
		Integer duration = ffmpegUtils.getVideoInfoDuration(completeVideo);
		updateFilePost.setDuration( duration);
		updateFilePost.setFilePath(Constants.FILE_VIDEO + videoFileInfo.getFilePath());
		updateFilePost.setFileSize(new File(completeVideo).length());
		updateFilePost.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getStatus());
		convertVideo2Ts(completeVideo);

		} catch (Exception e) {
			log.error("文件转码失败", e);
			updateFilePost.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
        } finally {
			//更新视频文件状态
			videoInfoFilePostMapper.updateByUploadIdAndUserId(updateFilePost, videoInfoFilePost.getUploadId(), videoInfoFilePost.getUserId());

			VideoInfoFilePostQuery videoInfoFilePostQuery = new VideoInfoFilePostQuery();
			videoInfoFilePostQuery.setVideoId(videoInfoFilePost.getVideoId());
			videoInfoFilePostQuery.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
			Integer failCount = videoInfoFilePostMapper.selectCount(videoInfoFilePostQuery);
			//如果文件转码失败 更新视频状态为转码失败
			if (failCount>0){
				VideoInfoPost videoInfoPost = new VideoInfoPost();
				videoInfoPost.setStatus(VideoStatusEnum.STATUS1.getStatus());
				videoInfoPostMapper.updateByVideoId(videoInfoPost, videoInfoFilePost.getVideoId());
				return;
			}

			//如果所有文件都转码成功 更新视频状态为待审核
			videoInfoFilePostQuery.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
			Integer transferCount = videoInfoFilePostMapper.selectCount(videoInfoFilePostQuery);
			if (transferCount==0){
				Integer duration = videoInfoFilePostMapper.sumDuration(videoInfoFilePost.getVideoId());
				VideoInfoPost videoInfoPost = new VideoInfoPost();
				videoInfoPost.setDuration(duration);
				videoInfoPost.setStatus(VideoStatusEnum.STATUS2.getStatus());
				videoInfoPostMapper.updateByVideoId(videoInfoPost, videoInfoFilePost.getVideoId());
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void auditVideo(String videoId, Integer status, String reason) {
		//校验审核状态
		VideoStatusEnum videoStatusEnum = VideoStatusEnum.getByStatus(status);
		if (videoStatusEnum == null){
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		VideoInfoPost videoInfoPost = new VideoInfoPost();
		videoInfoPost.setStatus(status);
		//修改视频状态
		VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
		videoInfoPostQuery.setVideoId(videoId);
		videoInfoPostQuery.setStatus(VideoStatusEnum.STATUS2.getStatus());
		Integer count = videoInfoPostMapper.updateByParam(videoInfoPost, videoInfoPostQuery);
		if (count == 0) {
			throw new BusinessException("审核失败，请刷新后重试");
		}
		//修改视频文件状态
		VideoInfoFilePost videoInfoFilePost = new VideoInfoFilePost();
		videoInfoFilePost.setUpdateType(VideoFileUpdateTypeEnum.NO_UPDATE.getStatus());
		VideoInfoFilePostQuery videoInfoFilePostQuery = new VideoInfoFilePostQuery();
		videoInfoFilePostQuery.setVideoId(videoId);
		videoInfoFilePostMapper.updateByParam(videoInfoFilePost, videoInfoFilePostQuery);

		//如果审核不通过
		if (VideoStatusEnum.STATUS4.equals(videoStatusEnum)){
			return;
		}

		VideoInfoPost infoPost = videoInfoPostMapper.selectByVideoId(videoId);
		VideoInfo dbVideoInfo =  videoInfoMapper.selectByVideoId(videoId);
		if (dbVideoInfo == null) {
			SysSettingDto sysSetting = redisComponent.getSysSetting();
			userInfoMapper.updateCoinCount(videoInfoPost.getUserId(), sysSetting.getPostVideoCoinCount());
		}
		//更新视频信息到正式表
		VideoInfo videoInfo = CopyTools.copy(infoPost, VideoInfo.class);
		videoInfoMapper.insertOrUpdate(videoInfo);

		//更新视频文件信息到正式表 先删除再插入
		VideoInfoFileQuery infoFileQuery = new VideoInfoFileQuery();
		infoFileQuery.setVideoId(videoId);
		videoInfoFileMapper.deleteByParam(infoFileQuery);

		VideoInfoFilePostQuery infoFilePostQuery = new VideoInfoFilePostQuery();
		infoFilePostQuery.setVideoId(videoId);
		List<VideoInfoFilePost> videoInfoFilePostList = videoInfoFilePostMapper.selectList(infoFilePostQuery);

		List<VideoInfoFile> videoInfoFileList = CopyTools.copyList(videoInfoFilePostList, VideoInfoFile.class);
		videoInfoFileMapper.insertBatch(videoInfoFileList);
		//删除上传文件
		List<String> delQueue = redisComponent.getFileFormDelQueue(videoId);
		if (delQueue != null) {
			for (String filePath : delQueue) {
				File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + filePath);
				if (file.exists()) {
					try {
						FileUtils.deleteDirectory(file);
					} catch (IOException e) {
						log.error("删除文件失败", e);
					}
				}
			}
		}
		redisComponent.cleanDelQueue(videoId);
	}


	private void convertVideo2Ts(String completeVideo) {
		File videoFile = new File(completeVideo);
		File tsFolder = videoFile.getParentFile();
		// 获取文件编码格式
		String codec = ffmpegUtils.getVideoCodec(completeVideo);
		// 保证格式：只有和h264的可以
		if (Constants.VIDEO_CODE_HEVC.equals(codec)) {
			// 生成一个临时文件名
			String tempFileName = completeVideo + Constants.VIDEO_CODE_TEMP_FILE_SUFFIX;
			// 重命名文件为临时文件名
			new File(completeVideo).renameTo(new File(tempFileName));
			// 将HEVC格式的文件转换为MP4格式
			ffmpegUtils.convertHevc2Mp4(tempFileName, completeVideo);
			// 删除临时文件
			new File(tempFileName).delete();
		}
		// mp4文件-> .ts文件+.m3u8索引文件
		ffmpegUtils.convertVideo2Ts(tsFolder, completeVideo);
		videoFile.delete();
	}


	// 分片->.mp4文件
	private void union(String dirPath, String toFilePath, Boolean delSource) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			throw new BusinessException("目录不存在");
		}

		File[] fileList = dir.listFiles();
		File targetFile = new File(toFilePath);
		try (RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {
			byte[] b = new byte[1024 * 10];
			for (int i = 0; i < fileList.length; i++) {
				int len = -1;
				// 创建读取文件的对象
				File chunkFile = new File(dirPath + File.separator + i);
				RandomAccessFile readFile = null;
				try {
					readFile = new RandomAccessFile(chunkFile, "r");
					while ((len = readFile.read(b)) != -1) {
						writeFile.write(b, 0, len);
					}
				} catch (Exception e) {
					log.error("合并分片失败", e);
					throw new BusinessException("合并文件失败");
				} finally {
					if (readFile != null) {
						readFile.close();
					}
				}
			}
		} catch (Exception e) {
			throw new BusinessException("合并文件" + dirPath + " 出错了");
		} finally {
			if (delSource) {
				for (int i = 0; i < fileList.length; i++) {
					fileList[i].delete();
				}
			}
		}
	}


	private Boolean changeVideoInfo(VideoInfoPost videoInfoPost) {
		VideoInfoPost dbInfo = this.videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
		// 标题, 封面, 标签, 简介是否有变化
		if (!videoInfoPost.getVideoName().equals(dbInfo.getVideoName())
				|| !videoInfoPost.getVideoCover().equals(dbInfo.getVideoCover())
				|| !videoInfoPost.getTags().equals(dbInfo.getTags())
				|| !videoInfoPost.getIntroduction().equals(dbInfo.getIntroduction() == null ? "" : dbInfo.getIntroduction())) {
			return true;
		} else {
			return false;
		}
	}


}