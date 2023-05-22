package com.sajayanegar.storage.dto;

public class GetDto {
    private String key;
    private String bucket;

    public GetDto(String key, String bucket) {
        this.key = key;
        this.bucket = bucket;
    }

    public GetDto() {
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
