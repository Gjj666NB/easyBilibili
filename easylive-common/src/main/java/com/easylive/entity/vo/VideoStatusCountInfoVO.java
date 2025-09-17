package com.easylive.entity.vo;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

public class VideoStatusCountInfoVO {
    private Integer auditSuccessCount;
    private Integer auditFailCount;
    private Integer inProcessCount;

    public Integer getAuditSuccessCount() {
        return auditSuccessCount;
    }

    public void setAuditSuccessCount(Integer auditSuccessCount) {
        this.auditSuccessCount = auditSuccessCount;
    }

    public Integer getAuditFailCount() {
        return auditFailCount;
    }

    public void setAuditFailCount(Integer auditFailCount) {
        this.auditFailCount = auditFailCount;
    }

    public Integer getInProcessCount() {
        return inProcessCount;
    }

    public void setInProcessCount(Integer inProcessCount) {
        this.inProcessCount = inProcessCount;
    }
}
