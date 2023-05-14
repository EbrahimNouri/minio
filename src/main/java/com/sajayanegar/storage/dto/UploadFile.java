package com.sajayanegar.storage.dto;

import org.springframework.web.multipart.MultipartFile;

public class UploadFile {

//    private String bucket;
//    private String key;
    private MultipartFile file;

    public UploadFile(MultipartFile file) {
        this.file = file;
    }

    public UploadFile() {
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
