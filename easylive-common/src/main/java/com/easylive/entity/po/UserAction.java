package com.easylive.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.easylive.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easylive.enums.DateTimePatternEnum;


/**
 * @description: 用户行为表
 * @date: 2025-03-07
 */
public class UserAction implements Serializable {
    /**
     * 自增ID
     */
    private Integer actionId;
    /**
     * 视频ID
     */
    private String videoId;
    /**
     * 视频作者ID
     */
    private String videoUserId;
    /**
     * 评论ID
     */
    private Integer commentId;
    /**
     * 0：评论喜欢 1：评论讨厌 2：视频点赞 3：视频收藏 4：视频投币
     */
    private Integer actionType;
    /**
     * 数量-（投币可以2）
     */
    private Integer actionCount;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actionTime;

    private String videoCover;

    private String videoName;

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoUserId(String videoUserId) {
        this.videoUserId = videoUserId;
    }

    public String getVideoUserId() {
        return videoUserId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionCount(Integer actionCount) {
        this.actionCount = actionCount;
    }

    public Integer getActionCount() {
        return actionCount;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public Date getActionTime() {
        return actionTime;
    }

    @Override
    public String toString() {
        return "自增ID:" + (actionId == null ? "空" : actionId) + " ,视频ID:" + (videoId == null ? "空" : videoId) + " ,视频作者ID:" + (videoUserId == null ? "空" : videoUserId) + " ,评论ID:" + (commentId == null ? "空" : commentId) + " ,0：评论喜欢 1：评论讨厌 2：视频点赞 3：视频收藏 4：视频投币:" + (actionType == null ? "空" : actionType) + " ,数量-（投币可以2）:" + (actionCount == null ? "空" : actionCount) + " ,用户ID:" + (userId == null ? "空" : userId) + " ,操作时间:" + (actionTime == null ? "空" : DateUtil.format(actionTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}