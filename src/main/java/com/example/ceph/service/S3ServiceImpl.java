package com.example.ceph.service;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.util.IOUtils;
import com.example.ceph.exception.FileSizeException;
import com.example.ceph.util.S3Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class S3ServiceImpl implements S3Service {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //    @Value("${s3.maxAge}")
    private int MAX_AGE_DAYS;

//    @Autowired
//    private AmazonS3 amazonS3;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Util s3Util;

    @Override
    public void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException {

        logger.info("Uploading file");

        ObjectMetadata metadata = new ObjectMetadata();

        // TODO: 4/19/2023 for limit in size of upload
        long sizeLimit = 10485760; // 10 MB in bytes

        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        if (file.getSize() <= sizeLimit) {


            // for auto remove files
//            Date date = new Date();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.DATE, MAX_AGE_DAYS);
//            date = calendar.getTime();
//            metadata.setExpirationTime(date);

            Instant instant = Instant.now().plus(7, ChronoUnit.DAYS);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .expires(instant)
                    .build();

            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        } else {

            logger.info("Uploading file failed");
            throw new FileSizeException("size file more than" + sizeLimit / 1_048_576 + " megabytes");
        }

    }

    public void uploadFile(String bucketName, String objectKey, File file) {

        // Create an S3Client objec

        // Create a PutObjectRequest object
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();

        // Upload the file to S3
        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromFile(file));

        // Print the result
        logger.debug("File uploaded to S3 with ETag " + response.eTag());
    }

    @Override
    public void deleteFile(String bucketName, String objectKey) {
        logger.debug("deleting file");
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(objectKey).build());
    }

    @Override
    public byte[] readFile(String bucketName, String objectKey) throws IOException {


        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
    }

    @Override
    public void createDirectory(String bucketName, String directoryName) {

        directoryName =  directoryName + "/";
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(directoryName)
                    .build();

        s3Client.putObject(request, RequestBody.fromBytes(new byte[0]));
    }

    @Override
    public void deleteObjectsInDirectory(String bucketName, String prefix) {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response listObjectsResponse;
        do {
            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            for (S3Object s3Object : listObjectsResponse.contents()) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Object.key())
                        .build());
            }

            listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .continuationToken(listObjectsResponse.nextContinuationToken())
                    .build();
        } while (listObjectsResponse.isTruncated());

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(prefix)
                .build());
    }

    @Override
    public void setBucketQuota(/*String bucketName, long quotaBytes*/) {
        s3Util.setBucketQuota(/*bucketName, quotaBytes*/);
        logger.info("limiting space storage");
    }


    // TODO: 4/30/2023 resolve that ↓
    public void showS3BucketStorageUsage(S3Client s3Client, String bucketName) throws IOException {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response result;

        long totalSize = 0;
        int totalObjects = 0;

        do {
            result = s3Client.listObjectsV2(listObjectsRequest);
            List<S3Object> objects = result.contents();

            for (S3Object object : objects) {
                totalSize += object.size();
                totalObjects++;
            }

            listObjectsRequest = listObjectsRequest.toBuilder()
                    .continuationToken(result.nextContinuationToken())
                    .build();
        } while(result.isTruncated());

        logger.debug("Total number of objects in bucket: " + totalObjects);
        logger.debug("Total size of objects in bucket: " + totalSize + " bytes");

        GetBucketLoggingRequest loggingConfigRequest = GetBucketLoggingRequest.builder()
                .bucket(bucketName)
                .build();

        GetBucketLoggingResponse loggingConfigResponse = s3Client.getBucketLogging(loggingConfigRequest);

        if (loggingConfigResponse.loggingEnabled().hasTargetGrants() && loggingConfigResponse.loggingEnabled().targetBucket() != null) {
            String logBucket = loggingConfigResponse.loggingEnabled().targetBucket();
            ResponseInputStream<GetObjectResponse> object = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(logBucket)
                    .key(loggingConfigResponse.loggingEnabled().targetPrefix())
                    .build());

            InputStream inputStream = object;
            long size = inputStream.available();
//            byte[] bytes = IOUtils.toByteArray(inputStream);
            logger.debug("Total size of access logs: " + size + " bytes");
        }
    }

    // TODO: 4/30/2023 must be checked
    public void backupDirectory(S3Client s3Client, String bucketName, String sourceKeyPrefix, String destinationPath) throws IOException {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(sourceKeyPrefix)
                .build();

        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        List<S3Object> s3Objects = listObjectsResponse.contents();

        for (S3Object s3Object : s3Objects) {
            String key = s3Object.key();
            String destinationFilePath = destinationPath + "/" + key.substring(sourceKeyPrefix.length());

            if (s3Object.size() > 0) {
                File file = new File(destinationFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

                s3Client.getObject(getObjectRequest, ResponseTransformer.toFile(file));
            } else {
                File directory = new File(destinationFilePath);
                directory.mkdirs();
            }
        }
    }


}
