package com.easylive.entity.query;

import org.springframework.data.elasticsearch.core.query.BaseQuery;

import java.util.Date;


/**
 * @description: 用户行为表查询对象
 * @date: 2025-03-07
 */
public class UserActionQuery extends BaseParam {
    /**
     * 自增ID
     */
    private Integer actionId;

    /**
     * 视频ID
     */
    private String videoId;

    private String videoIdFuzzy;

    /**
     * 视频作者ID
     */
    private String videoUserId;

    private String videoUserIdFuzzy;

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

    private String userIdFuzzy;

    /**
     * 操作时间
     */
    private Date actionTime;

    private String actionTimeStart;

    private String actionTimeEnd;

    private Integer[] actionTypeArray;

    private Boolean queryVideoInfo;

    public Boolean getQueryVideoInfo() {
        return queryVideoInfo;
    }

    public void setQueryVideoInfo(Boolean queryVideoInfo) {
        this.queryVideoInfo = queryVideoInfo;
    }

    public Integer[] getActionTypeArray() {
        return actionTypeArray;
    }

    public void setActionTypeArray(Integer[] actionTypeArray) {
        this.actionTypeArray = actionTypeArray;
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

    public void setVideoIdFuzzy(String videoIdFuzzy) {
        this.videoIdFuzzy = videoIdFuzzy;
    }

    public String getVideoIdFuzzy() {
        return videoIdFuzzy;
    }

    public void setVideoUserIdFuzzy(String videoUserIdFuzzy) {
        this.videoUserIdFuzzy = videoUserIdFuzzy;
    }

    public String getVideoUserIdFuzzy() {
        return videoUserIdFuzzy;
    }

    public void setUserIdFuzzy(String userIdFuzzy) {
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy() {
        return userIdFuzzy;
    }

    public void setActionTimeStart(String actionTimeStart) {
        this.actionTimeStart = actionTimeStart;
    }

    public String getActionTimeStart() {
        return actionTimeStart;
    }

    public void setActionTimeEnd(String actionTimeEnd) {
        this.actionTimeEnd = actionTimeEnd;
    }

    public String getActionTimeEnd() {
        return actionTimeEnd;
    }

}