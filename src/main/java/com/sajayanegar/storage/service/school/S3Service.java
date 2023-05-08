package com.sajayanegar.storage.service.school;

import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public interface S3Service {

    void createNewUserStorageUser(String bucketName);

    void createBucket(String bucket);

    // TODO: 5/1/2023 1)
    void uploadFile(String bucketName, String objectKey, File file) throws IOException;

    void uploadFile(PutObjectRequest putObjectRequest);

    void deleteFile(String bucketName, String objectKey);

    byte[] readFile(String bucketName, String objectKey) throws IOException;

    void createDirectory(String bucketName, String directoryName);

    void deleteObjectsInDirectory(String bucketName, String directoryName);

    void setBucketQuota(String bucketName, long quotaBytes);


    // TODO: 4/30/2023 resolve that ↓ 15)
    void showBucketStorageUsage(String bucketName) throws IOException;

    void backupDirectory(String bucketName, String sourceKeyPrefix, String destinationPath) throws IOException;

    // TODO: 5/2/2023 5)
    Long getFileSize(String bucketName, String objectKey);

    // TODO: 5/2/2023 6)
    Map<String, String> getFileFormatAndUploadTime(String bucketName, String objectKey);

    // TODO: 5/2/2023 8)
    void uploadZip(String bucketName, String key, File file) throws FileNotFoundException;

    // TODO: 5/2/2023 9)
    File downloadDirectoryAsZip(String bucketName, String directoryKey) throws IOException;

    long getFolderSize(String bucketName, String folderKey);

    Date getFolderCreationDate(String bucketName, String folderKey);

}
