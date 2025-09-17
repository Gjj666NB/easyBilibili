package com.easylive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

@Configuration
public class AppConfig {
    @Value("${project.folder:}")
    private String projectFolder;

    @Value("${admin.account:}")
    private String adminAccount;

    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${show.ffmpeg.log:true}")
    private Boolean showFFmpegLog;

    @Value("${es.host.port:localhost:9200}")
    private String esHostPort;

    @Value("${es.index.video.name:easylive_video}")
    private String esIndexVideoName;

    public String getEsIndexVideoName() {
        return esIndexVideoName;
    }

    public String getEsHostPort() {
        return esHostPort;
    }

    public Boolean getShowFFmpegLog() {
        return showFFmpegLog;
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }
}
