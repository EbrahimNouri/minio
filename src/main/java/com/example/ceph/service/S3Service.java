package com.example.ceph.service;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.Date;

public interface S3Service {

    void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException;

    void deleteFile(String bucketName, String objectKey);

    byte[] readFile(String bucketName, String objectKey) throws IOException;

    void createDirectory(String bucketName, String directoryName);

    void deleteObjectsInDirectory(String bucketName, String directoryName);

    void setBucketQuota(/*String bucketName, long quotaBytes*/);

    // TODO: 4/30/2023 resolve that ↓
    void showS3BucketStorageUsage(S3Client s3Client, String bucketName) throws IOException;

    void backupDirectory(S3Client s3Client, String bucketName, String sourceKeyPrefix, String destinationPath) throws IOException;

    long getFolderSize(String bucketName, String folderKey);

    Date getFolderCreationDate(S3Client s3Client, String bucketName, String folderKey);
}
