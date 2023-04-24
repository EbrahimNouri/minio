package com.example.ceph.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException;

    void deleteFile(String bucketName, String objectKey);

    byte[] readFileFromS3(String bucketName, String objectKey) throws IOException;

    void createDirectory(String bucketName, String directoryName);

    void deleteObjectsInDirectory(String bucketName, String directoryName);


}
