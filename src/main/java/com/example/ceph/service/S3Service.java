package com.example.ceph.service;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public interface S3Service {

    void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException;

    void deleteFile(String bucketName, String objectKey);

    byte[] readFile(String bucketName, String objectKey) throws IOException;

    void createDirectory(String bucketName, String directoryName);

    void deleteObjectsInDirectory(String bucketName, String directoryName);

    void setBucketQuota(/*String bucketName, long quotaBytes*/);


    // TODO: 4/30/2023 resolve that ↓ 15)
    void showBucketStorageUsage(String bucketName) throws IOException;

    void backupDirectory(String bucketName, String sourceKeyPrefix, String destinationPath) throws IOException;

    // TODO: 5/2/2023 5)
    Long getFileSize(String bucketName, String objectKey);

    // TODO: 5/2/2023 6)
    Map<String, String> getFileFormatAndUploadTime(String bucketName, String objectKey);

    // TODO: 5/2/2023 8)
    void uploadZip(String bucketName, String key, File file);

    // TODO: 5/2/2023 9)
    File downloadDirectoryAsZip(String bucketName, String directoryKey) throws IOException;

    long getFolderSize(String bucketName, String folderKey);

    Date getFolderCreationDate(String bucketName, String folderKey);

    void saveTicket(String bucketName, String ticketId, String ticketContent);

    void replyToTicket(String bucketName, String ticketId, String replyMessage);

    void closeTicket(String bucketName, String ticketId);
}
