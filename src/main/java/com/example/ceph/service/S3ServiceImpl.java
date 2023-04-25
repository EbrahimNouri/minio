package com.example.ceph.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.example.ceph.exception.FileSizeException;
import com.example.ceph.util.S3Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class S3ServiceImpl implements S3Service {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Value("${s3.maxAge}")
    private int MAX_AGE_DAYS;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private S3Util s3Util;

    public S3ServiceImpl(AmazonS3 amazonS3, S3Util s3Util) {
        this.amazonS3 = amazonS3;
        this.s3Util = s3Util;
    }

    @Override
    public void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException {

        logger.info("Uploading file");

        ObjectMetadata metadata = new ObjectMetadata();

        // TODO: 4/19/2023 for limit in size of upload
        long sizeLimit = 10485760; // 10 MB in bytes

        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        if (file.getSize() <= sizeLimit) {

            PutObjectRequest request = new PutObjectRequest(
                    bucketName, objectKey, file.getInputStream(), new ObjectMetadata());

            PutObjectResult putObjectResult = amazonS3.putObject(request);

            // for auto remove files
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, MAX_AGE_DAYS);
            date = calendar.getTime();
            putObjectResult.setExpirationTime(date);


        } else {

            logger.info("Uploading file failed");
            throw new FileSizeException("size file more than" + sizeLimit / 1_048_576 + " megabytes");
        }

    }

    @Override
    public void deleteFile(String bucketName, String objectKey) {
        logger.debug("deleting file");
        amazonS3.deleteObject(bucketName, objectKey);
    }

    @Override
    public byte[] readFileFromS3(String bucketName, String objectKey) throws IOException {

        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        S3Object s3Object = s3Client.getObject(bucketName, objectKey);
        InputStream inputStream = s3Object.getObjectContent();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        byte[] data = outputStream.toByteArray();
        outputStream.close();
        inputStream.close();

        logger.info("reading data from S3 bucket {}", bucketName);

        return data;
    }

    @Override
    public void createDirectory(String bucketName, String directoryName) {
        // Add a trailing slash to the directory name to indicate that it's a "directory"
        String key = directoryName + "/";

        // Create an empty object with the "directory" key
        byte[] emptyContent = new byte[0];
        InputStream emptyInputStream = new ByteArrayInputStream(emptyContent);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        PutObjectRequest request = new PutObjectRequest(bucketName, key, emptyInputStream, metadata);

        // Upload the empty object to S3

        logger.info("Uploading directory {}", directoryName);
        amazonS3.putObject(request);
    }

    @Override
    public void deleteObjectsInDirectory(String bucketName, String directoryName) {
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(directoryName + "/");

        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(listObjectsRequest);
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            for (S3ObjectSummary os : objects) {
                amazonS3.deleteObject(bucketName, os.getKey());
            }
            String token = result.getNextContinuationToken();
            listObjectsRequest.setContinuationToken(token);
        } while (result.isTruncated());

        logger.info("ListObjectsV2 Response from Amazon S3 bucket " + bucketName + " running");
    }

    @Override
    public void setBucketQuota(/*String bucketName, long quotaBytes*/) {
        s3Util.setBucketQuota(/*bucketName, quotaBytes*/);
        logger.info("limiting space storage");
    }

}
