package com.easylive.mappers;

import com.easylive.entity.dto.CountInfoDto;
import org.apache.ibatis.annotations.Param;

/**
 * @description: 视频信息Mapper
 * @date: 2025-03-06
 */
public interface VideoInfoMapper<T, P> extends BaseMapper<T, P> {
	/**
	 * 根据VideoId查询
	 */
	T selectByVideoId(@Param("videoId") String videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateByVideoId(@Param("bean") T t, @Param("videoId") String videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteByVideoId(@Param("videoId") String videoId);

	void updateCountInfo(@Param("videoId") String videoId, @Param("field") String field, @Param("changeCount") Integer changeCount);
}