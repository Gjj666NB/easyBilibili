package com.easylive.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

public class UserInfoVo {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 0：女 1：男 2：未知
     */
    private Integer sex;
    /**
     * 出生日期
     */
    private String birthday;
    /**
     * 学校
     */
    private String school;
    /**
     * 个人简介
     */
    private String personIntroduction;
    /**
     * 空间公告
     */
    private String noticeInfo;
    /**
     * 主题
     */
    private Integer theme;

    private Integer fansCount;

    private Integer focusCount;

    private Integer likeCount;

    private Integer playCount;

    private Boolean haveFocus;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getpersonIntroduction() {
        return personIntroduction;
    }

    public void setpersonIntroduction(String personIntroduction) {
        this.personIntroduction = personIntroduction;
    }

    public String getNoticeInfo() {
        return noticeInfo;
    }

    public void setNoticeInfo(String noticeInfo) {
        this.noticeInfo = noticeInfo;
    }

    public Integer getTheme() {
        return theme;
    }

    public void setTheme(Integer theme) {
        this.theme = theme;
    }

    public Integer getFansCount() {
        return fansCount;
    }

    public void setFansCount(Integer fansCount) {
        this.fansCount = fansCount;
    }

    public Integer getFocusCount() {
        return focusCount;
    }

    public void setFocusCount(Integer focusCount) {
        this.focusCount = focusCount;
    }

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

    public Boolean getHaveFocus() {
        return haveFocus;
    }

    public void setHaveFocus(Boolean haveFocus) {
        this.haveFocus = haveFocus;
    }
}
