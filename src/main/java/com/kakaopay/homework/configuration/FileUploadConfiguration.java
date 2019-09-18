package com.kakaopay.homework.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileUploadConfiguration {
    private String uploadDir;

    public void setUploadDir (String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUploadDir () {
        return uploadDir;
    }
}
