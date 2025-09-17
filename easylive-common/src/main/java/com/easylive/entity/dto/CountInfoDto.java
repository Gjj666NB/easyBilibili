package com.easylive.entity.dto;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

public class CountInfoDto {
    private Integer playCount;
    private Integer likeCount;

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }
}
