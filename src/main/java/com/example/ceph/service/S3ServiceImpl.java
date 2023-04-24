package com.example.ceph.service;

import java.io.*;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.example.ceph.util.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.amazonaws.services.s3.AmazonS3;

@Service
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;

    private final S3Util s3Util;

    public S3ServiceImpl(AmazonS3 amazonS3, S3Util s3Util) {
        this.amazonS3 = amazonS3;
        this.s3Util = s3Util;
    }

    @Override
    public void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();

        // TODO: 4/19/2023 for limit in size of upload
        long sizeLimit = 10485760; // 10 MB in bytes

        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        if (file.getSize() <= sizeLimit) {

            PutObjectRequest request = new PutObjectRequest(
                    bucketName, objectKey, file.getInputStream(), new ObjectMetadata());

            amazonS3.putObject(request);
        } else {
            throw new FileNotFoundException("size file more than" + sizeLimit / 1_048_576 + " megabytes");
        }

    }

    @Override
    public void deleteFile(String bucketName, String objectKey) {
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
        amazonS3.putObject(request);
    }

    @Override
    public void deleteObjectsInDirectory(String bucketName, String directoryName) {

    }

    @Autowired
    public void setBucketQuota(/*String bucketName, long quotaBytes*/) {
        s3Util.setBucketQuota(/*bucketName, quotaBytes*/);
    }

}
