package com.easylive.entity.vo;

import com.easylive.entity.po.UserVideoSeries;
import com.easylive.entity.po.UserVideoSeriesVideo;

import java.util.List;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

public class UserVideoSeriesDetailVo {
    private UserVideoSeries userVideoSeries;
    private List<UserVideoSeriesVideo> userVideoSeriesVideos;

    public UserVideoSeries getUserVideoSeries() {
        return userVideoSeries;
    }

    public void setUserVideoSeries(UserVideoSeries userVideoSeries) {
        this.userVideoSeries = userVideoSeries;
    }

    public List<UserVideoSeriesVideo> getUserVideoSeriesVideos() {
        return userVideoSeriesVideos;
    }

    public void setUserVideoSeriesVideos(List<UserVideoSeriesVideo> userVideoSeriesVideos) {
        this.userVideoSeriesVideos = userVideoSeriesVideos;
    }
}
