package com.example.ceph.service;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.ceph.exception.DeleteTicketException;
import com.example.ceph.exception.TicketClosedException;
import com.example.ceph.util.S3Util;
import com.example.ceph.util.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    private ZipUtil zipUtil;

    @Autowired
    private S3Util s3Util;

    // TODO: 5/1/2023 1)
    @Override
    public void uploadFile(String bucketName, String objectKey, MultipartFile file) throws IOException {

        logger.info("Uploading file");

        ObjectMetadata metadata = new ObjectMetadata();

        // TODO: 4/19/2023 for limit in size of upload
//        long sizeLimit = 10485760; // 10 MB in bytes

        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

//        if (file.getSize() <= sizeLimit) {


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

//        } else {
//
//            logger.info("Uploading file failed");
//            throw new FileSizeException("size file more than" + sizeLimit / 1_048_576 + " megabytes");
//        }

    }

    public void simpleUploadFile(String bucketName, String objectKey, File file) {

        // Create an S3Client object

        // Create a PutObjectRequest object
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();

        // Upload the file to S3
        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromFile(file));

        // Print the result
        logger.debug("File uploaded to S3 with ETag " + response.eTag());
    }

    // TODO: 5/1/2023 3)
    @Override
    public void deleteFile(String bucketName, String objectKey) {
        logger.debug("deleting file");
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(objectKey).build());
    }

    // TODO: 5/1/2023 2)
    @Override
    public byte[] readFile(String bucketName, String objectKey) {

        containTicket(objectKey);


        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
    }

    private void containTicket(String objectKey) {
        if (objectKey.contains("/ticket"))
            throw new DeleteTicketException(objectKey);
    }

    // TODO: 5/1/2023 7)
    @Override
    public void createDirectory(String bucketName, String directoryName) {

        directoryName = directoryName + "/";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(directoryName)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(new byte[0]));
    }

    // TODO: 5/1/2023 10)
    @Override
    public void deleteObjectsInDirectory(String bucketName, String prefix) {

        containTicket(prefix);

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

    // TODO: 5/1/2023 14)
    @Override
    public void setBucketQuota(/*String bucketName, long quotaBytes*/) {
        s3Util.setBucketQuota(/*bucketName, quotaBytes*/);
        logger.info("limiting space storage");
    }


    // TODO: 4/30/2023 resolve that ↓ 15)
    @Override
    public void showBucketStorageUsage(String bucketName) throws IOException {
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
        } while (result.isTruncated());

        logger.info("Total number of objects in bucket: " + totalObjects);
        logger.info("Total size of objects in bucket: " + totalSize + " bytes");

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

    // TODO: 4/30/2023 must be checked 11) 4)
    @Override
    public void backupDirectory(String bucketName, String sourceKeyPrefix, String destinationPath) throws IOException {

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

    // TODO: 5/2/2023 5)
    @Override
    public Long getFileSize(String bucketName, String objectKey) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        HeadObjectResponse metadata = s3Client.headObject(headObjectRequest);
        return metadata.contentLength();

    }

    // TODO: 5/2/2023 6)
    @Override
    public Map<String, String> getFileFormatAndUploadTime(String bucketName, String objectKey) {
        Map<String, String> map;
        Map<String, String> returnMap = new HashMap<>();

        HeadObjectRequest objectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

            map = headObjectResponse.metadata();
            String fileFormat = map.get("Content-Type");
            String uploadTime = map.get("Last-Modified");
            returnMap.put("File format: ", fileFormat);
            returnMap.put("Upload time: ", uploadTime);
            return returnMap;
        } catch (AmazonServiceException e) {
            // handle exception
        }
        return new HashMap<>();
    }

    // TODO: 5/2/2023 8)
    @Override
    public void uploadZip(String bucketName, String key, File file) {


        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());
        metadata.setContentType("application/zip");

        // Upload the file to S3
        simpleUploadFile(bucketName,key, file);

        // Extract the files and directories from the zip file and upload them to S3
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                String entryName = entry.getName();
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(entryName)
                        .metadata(new ObjectMetadata().getUserMetadata())
                        .build();

                if (!entry.isDirectory()) {
                    // Upload file to S3
                    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(entry.getExtra()));

                } else {
                    // Create directory in S3
                    s3Client.putObject(putObjectRequest,/* entryName + "/", new byte[0], new ObjectMetadata().getUserMetadata()*/
                    RequestBody.fromBytes(new byte[0]));

                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        } catch (AmazonServiceException e) {
            logger.error(e.getErrorMessage());
            System.exit(1);
        } catch (AmazonClientException | IOException e) {
            logger.error(e.getMessage());
        }
    }

    // TODO: 5/2/2023 9)
    @Override
    public File downloadDirectoryAsZip(String bucketName, String directoryKey) throws IOException {
        // Create a new temporary directory to store the downloaded files
        Path tempDir = Files.createTempDirectory("downloaded-files");

        // Create a new ZIP file to store the downloaded directory
        File zipFile = File.createTempFile("downloaded-directory", ".zip");

        // List all objects in the specified directory
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(directoryKey)
                .build();
        ListObjectsV2Response listObjectsResponse;
        do {
            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            for (S3Object object : listObjectsResponse.contents()) {

                // Get the object key and file name
                String objectKey = object.key();
                String fileName = objectKey.substring(directoryKey.length() + 1); // Remove the directory prefix

                // Download the object to the temporary directory
                Path filePath = Paths.get(tempDir.toString(), fileName);
                s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build(), ResponseTransformer.toFile(filePath));
            }
            listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(directoryKey)
                    .continuationToken(listObjectsResponse.nextContinuationToken())
                    .build();
        } while (listObjectsResponse.isTruncated());

        // Zip the downloaded directory and its contents
        ZipUtil.zipDirectory(tempDir.toFile().toPath(), zipFile.toPath());

        // Delete the temporary directory and its contents
        FileUtils.deleteDirectory(tempDir.toFile());

        // Return the ZIP file
        return zipFile;
    }

    // TODO: 5/1/2023 12)
    @Override
    public long getFolderSize(String bucketName, String folderKey) {
        long size = 0;

        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderKey + "/")
                .build();

        ListObjectsV2Response listObjectsResponse;
        do {
            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            for (S3Object object : listObjectsResponse.contents()) {
                size += object.size();
            }

            listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folderKey + "/")
                    .continuationToken(listObjectsResponse.nextContinuationToken())
                    .build();
        } while (listObjectsResponse.isTruncated());

        return size;
    }

    // TODO: 5/1/2023 13)
    @Override
    public Date getFolderCreationDate(String bucketName, String folderKey) {
        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderKey + "/")
                .build();

        ListObjectsV2Response listObjectsResponse;
        do {
            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

            for (S3Object objectSummary : listObjectsResponse.contents()) {
                if (objectSummary.key().equals(folderKey + "/")) {

                    return Date.from(objectSummary.lastModified());
                }
            }

            listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folderKey + "/")
                    .continuationToken(listObjectsResponse.nextContinuationToken())
                    .build();
        } while (listObjectsResponse.isTruncated());

        return null;
    }

    @Override
    public void saveTicket(String bucketName, String ticketId, String ticketContent) {

        InputStream inputStream = new ByteArrayInputStream(ticketContent.getBytes());
        Map<String, String> metadata = new HashMap<>();
        metadata.put("isTicketClosed", "false");
        metadata.put("ticketId", ticketId);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(ticketId)
                .acl(ObjectCannedACL.PRIVATE)
                .metadata(metadata)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, ticketContent.getBytes().length));
        logger.info("Ticket " + ticketId + " uploaded to bucket " + bucketName);
    }

    @Override
    public void replyToTicket(String bucketName, String ticketId, String replyMessage) {
        // Create a new object in the bucket with the reply message as its content
        byte[] contentBytes = replyMessage.getBytes(StandardCharsets.UTF_8);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("ticketId", ticketId);


        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(ticketId + "/reply.txt")
                .metadata(metadata)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(replyMessage.getBytes()));

        // Update the ticket status to "replied"
        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("status", "replied");
        metadataMap.put("lastUpdated", String.valueOf(System.currentTimeMillis()));
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .copySource(bucketName + "/" + ticketId + "/")
                .destinationBucket(bucketName)
                .destinationKey(ticketId + "/")
                .metadata(metadataMap)
                .metadataDirective(MetadataDirective.REPLACE)
                .build();

        s3Client.copyObject(copyObjectRequest);
    }

    public void replyToTicket(String bucketName, String ticketId, String replyMessage, boolean isTicketClosed) {
        try {

            // Get the metadata of the ticket file
            Map<String, String> metadata = getMetadata(bucketName, ticketId);


            // Upload the reply message to S3
            String replyKey = ticketId + "/" + UUID.randomUUID();
            InputStream inputStream = new ByteArrayInputStream(replyMessage.getBytes(StandardCharsets.UTF_8));
            ObjectMetadata replyMetadata = new ObjectMetadata();
            replyMetadata.setContentLength(replyMessage.getBytes(StandardCharsets.UTF_8).length);
            replyMetadata.setContentType("text/plain");
            replyMetadata.setUserMetadata(Collections.singletonMap("isTicketClosed", Boolean.toString(isTicketClosed)));
            s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(replyKey).metadata(replyMetadata.getUserMetadata()).build(), RequestBody.fromInputStream(inputStream, replyMessage.getBytes(StandardCharsets.UTF_8).length));

            // Set the new metadata for the ticket file
            changeMetadata(bucketName, ticketId, metadata, isTicketClosed);

        } catch (AmazonServiceException e) {
            // Handle Amazon service exceptions
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Handle SDK client exceptions
            e.printStackTrace();
        }
    }


    @Override
    public void closeTicket(String bucketName, String ticketId) {

        try {

            // Get the metadata of the ticket file
            Map<String, String> metadata = getMetadata(bucketName, ticketId);

            // Set the new metadata for the ticket file
            changeMetadata(bucketName, ticketId, metadata, true);

            logger.info("Closing the ticket file " + bucketName + " with metadata " + metadata);

        } catch (AmazonServiceException e) {
            // Handle Amazon service exceptions
            logger.error(e.getMessage());

        } catch (SdkClientException e) {
            // Handle SDK client exceptions
            logger.error(e.getMessage());
        }
    }

    private Map<String, String> getMetadata(String bucketName, String ticketId) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(ticketId)
                .build();
        HeadObjectResponse response = s3Client.headObject(request);
        Map<String, String> metadata = response.metadata();

        logger.info(getMetadata(bucketName, ticketId).toString());

        // Check if the ticket is closed
        if (metadata.containsKey("isTicketClosed") && Boolean.parseBoolean(metadata.get("isTicketClosed")))
            throw new TicketClosedException("Cannot reply to a closed ticket.");
        return metadata;
    }

    private void changeMetadata(String bucketName, String ticketId, Map<String, String> metadata, boolean isTicketClosed) {
        metadata.put("isTicketClosed", Boolean.toString(isTicketClosed));
        ObjectMetadata newMetadata = new ObjectMetadata();
        newMetadata.setContentLength(0L);
        newMetadata.setUserMetadata(metadata);

        /* Copy the ticket file to itself with the new metadata */
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .copySource(bucketName + "/" + ticketId)
                .destinationBucket(bucketName)
                .destinationKey(ticketId)
                .metadataDirective(MetadataDirective.REPLACE)
                .metadata(newMetadata.getUserMetadata())
                .build();
        s3Client.copyObject(copyRequest);
    }
}


