package com.easylive.entity.vo;

import com.easylive.entity.po.VideoInfo;
import lombok.Data;

import java.util.List;


public class VideoInResultVO {
    private VideoInfo videoInfo;

    private List userActionList;

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public List getUserActionList() {
        return userActionList;
    }

    public void setUserActionList(List userActionList) {
        this.userActionList = userActionList;
    }

    public VideoInResultVO(VideoInfo videoInfo, List userActionList) {
        this.videoInfo = videoInfo;
        this.userActionList = userActionList;
    }

    public VideoInResultVO() {
    }
}
