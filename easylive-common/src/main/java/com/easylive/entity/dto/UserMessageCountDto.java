package com.easylive.entity.dto;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

public class UserMessageCountDto {
    public Integer messageType;

    public Integer messageCount;

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }
}
