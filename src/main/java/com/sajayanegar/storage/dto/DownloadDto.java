package com.sajayanegar.storage.dto;

public class DownloadDto {
    private String key;
    private String bucket;

    public DownloadDto(String key, String bucket) {
        this.key = key;
        this.bucket = bucket;
    }

    public DownloadDto() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
