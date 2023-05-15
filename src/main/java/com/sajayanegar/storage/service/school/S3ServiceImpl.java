package com.sajayanegar.storage.service.school;


import com.sajayanegar.storage.exception.BucketExistException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import io.minio.messages.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

@Component
public class S3ServiceImpl implements S3Service {

    private final Long NATIONAL_CODE = new Random(10000L).nextLong();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private String uuid = UUID.randomUUID().toString();

//    @Value("${s3.maxAge}")
//    private int MAX_AGE_DAYS;

//    @Autowired
//    private AmazonS3 amazonS3;

    @Autowired
    private MinioClient s3Client;

//    @Autowired
//    private ZipUtil zipUtil;
//
//    @Autowired
//    private S3Util s3Util;

//    @Override
//    public void createNewUserStorageUser(String bucketName) {
//        uuid = UUID.randomUUID().toString();
//        createDirectory(bucketName, "/" + uuid);
//
//        //create directory's storage user and save nationalId and UUID save to database
//
//    }

    @Override
    public void createBucket(String bucket) {

        try {
            if (s3Client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build()))
                throw new BucketExistException("Bucket already exists for " + bucket);


            s3Client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.error(e.getMessage(), e);
        }
    }

    // TODO: 5/1/2023 1)
    @Override
    public void uploadFile(String bucketName, String objectKey, MultipartFile file) {

//        if (readFile(bucketName, "/" + uuid) == null)
//            //search in database for nationalCode and get uuid from database
//            createNewUserStorageUser(bucketName);
//
//        else {
//            logger.info("Uploading file");

//        ObjectMetadata metadata = new ObjectMetadata();
//
//        // TODO: 4/19/2023 for limit in size of upload
////        long sizeLimit = 10485760; // 10 MB in bytes
//
//        metadata.setContentLength(file.getSize());
//        metadata.setContentType(file.getContentType());

//        if (file.getSize() <= sizeLimit) {


        // for auto remove files
//            Date date = new Date();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.DATE, MAX_AGE_DAYS);
//            date = calendar.getTime();
//            metadata.setExpirationTime(date);


//        Instant instant = Instant.now().plus(7, ChronoUnit.DAYS);

        try {
            s3Client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
        } catch (ErrorResponseException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 InsufficientDataException e) {
            logger.error(e.getMessage(), e);
        }
    }

//    @Override
//    public void uploadFile(PutObjectRequest putObjectRequest) {
//
//        logger.info("Uploading file");

//        ObjectMetadata metadata = new ObjectMetadata();
//
//        // TODO: 4/19/2023 for limit in size of upload
////        long sizeLimit = 10485760; // 10 MB in bytes
//
//        metadata.setContentLength(file.getSize());
//        metadata.setContentType(file.getContentType());

//        if (file.getSize() <= sizeLimit) {


    // for auto remove files
//            Date date = new Date();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.DATE, MAX_AGE_DAYS);
//            date = calendar.getTime();
//            metadata.setExpirationTime(date);

//        Instant instant = Instant.now().plus(7, ChronoUnit.DAYS);


//        s3Client.putObject(putObjectRequest);

//        } else {
//
//            logger.error("Uploading file failed");
//            throw new FileSizeException("size file more than" + sizeLimit / 1_048_576 + " megabytes");
//        }

//    }


    // TODO: 5/1/2023 3)
    @Override
    public void deleteFile(String bucketName, String objectKey) {
        logger.debug("deleting file");

        try {
            s3Client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
//    // TODO: 5/1/2023 2)
    @Override
    public byte[] readFile(String bucketName, String objectKey) {

        try {
            return s3Client.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey).build()).readAllBytes();
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    //        byte[] data = outputStream.toByteArray();
//        outputStream.close();
//        inputStream.close();
//
//        return data;
//
//    }
//
//
//    // TODO: 5/1/2023 7)
    @Override
    public void createDirectory(String bucketName, String directoryName) {

        // Add a trailing slash to the directory name to indicate that it's a "directory"
        String key = directoryName + "/";

        // Create an empty object with the "directory" key
        byte[] emptyContent = new byte[0];
        InputStream emptyInputStream = new ByteArrayInputStream(emptyContent);
        ObjectMetadata metadata = new ObjectMetadata();
        // TODO: 5/14/2023 set created date to metadata
        PutObjectArgs request = PutObjectArgs.builder().bucket(bucketName).object(key)
                .userMetadata(metadata.userMetadata()).stream(emptyInputStream, 0, -1).build();

        // Upload the empty object to S3
        try {
            s3Client.putObject(request);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.error(e.getMessage(), e);
        }
    }

    //
    // TODO: 5/1/2023 10) not working properly
    @Override
    public void deleteObjectsInDirectory(String bucketName, String prefix) {
        try {
            Iterable<Result<Item>> results = s3Client.listObjects(ListObjectsArgs.builder().prefix(prefix)
                    .bucket(bucketName).build());

            for (Result<Item> result : results) {
                Item item = result.get();
                s3Client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
            }
            // حذف دایرکتوری خود دستی
            s3Client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(prefix).build());
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Failed to delete directory " + e.getMessage(), e);
        }
    }

    // TODO: 5/1/2023 14)
    @Override
    public void setBucketQuota(String bucketName, long quotaBytes) {
        try {
            if (s3Client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build())) {

                String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal" +
                        "\":{\"AWS\":\"*\"},\"Action\":[\"s3:ListBucket\",\"s3:GetBucketLocation\"],\"Resource" +
                        "\":[\"arn:aws:s3:::" + bucketName + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":" +
                        "\"*\"},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*" +
                        "\"],\"Condition\":{\"NumericLessThanEquals\":{\"s3:object-size\":" + quotaBytes + "}}}]}";


                s3Client.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policy)
                                .build()
                );
            }
        } catch (ServerException | InsufficientDataException | IOException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidResponseException | XmlParserException | InternalException |
                 ErrorResponseException e) {
            logger.error(e.getMessage(), e);
        }
    }
//
//
//    // TODO: 4/30/2023 resolve that ↓ 15)
//    @Override
//    public void showBucketStorageUsage(String bucketName) throws IOException {
//        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
//        listObjectsRequest.setBucketName(bucketName);
//
//        ListObjectsV2Result result;
//
//        long totalSize = 0;
//        int totalObjects = 0;
//
//        do {
//            result = s3Client.listObjectsV2(listObjectsRequest);
//            List<S3ObjectSummary> objects = result.getObjectSummaries();
//
//            for (S3ObjectSummary object : objects) {
//                totalSize += object.getSize();
//                totalObjects++;
//            }
//
//            listObjectsRequest.withContinuationToken(result.getNextContinuationToken());
//
//        } while (result.isTruncated());
//
//        logger.info("Total number of objects in bucket: " + totalObjects);
//        logger.info("Total size of objects in bucket: " + totalSize + " bytes");
//
//        BucketLoggingConfiguration loggingConfig = s3Client.getBucketLoggingConfiguration(bucketName);
//
//        if (loggingConfig != null && loggingConfig.getDestinationBucketName() != null) {
//            String logBucket = loggingConfig.getDestinationBucketName();
//            String logPrefix = loggingConfig.getLogFilePrefix();
//
//            S3Object logObject = s3Client.getObject(logBucket, logPrefix);
//            InputStream inputStream = logObject.getObjectContent();
//
//            byte[] bytes = IOUtils.toByteArray(inputStream);
//            long size = bytes.length;
//
//            logger.debug("Total size of access logs: " + size + " bytes");
//        }
//    }
//
//
//    // TODO: 4/30/2023 must be checked 11) 4)
//    @Override
//    public void backupDirectory(String bucketName, String sourceKeyPrefix, String destinationPath) throws IOException {
//
//        List<S3ObjectSummary> s3Objects = s3Util.getBackup(bucketName, sourceKeyPrefix);
//
//        for (S3ObjectSummary s3Object : s3Objects) {
//            String key = s3Object.getKey();
//            String destinationFilePath = destinationPath + "/" + key.substring(sourceKeyPrefix.length());
//
//            // TODO: 5/8/2023 check ↓
//
//            if (s3Object.getSize() > 0) {
//                File file = new File(destinationFilePath);
//                file.getParentFile().mkdir();
//                file.createNewFile();
//
//            } else {
//                File directory = new File(destinationFilePath);
//                directory.mkdirs();
//            }
//        }
//    }
//
//    // TODO: 5/2/2023 5)
//    @Override
//    public Long getFileSize(String bucketName, String objectKey) {
//
//        S3Object object = s3Client.getObject(bucketName, objectKey);
//        return object.getObjectMetadata().getContentLength();
//
//    }
//
//
//    // TODO: 5/2/2023 6)
//    @Override
//    public Map<String, String> getFileFormatAndUploadTime(String bucketName, String objectKey) {
//
//        Map<String, String> returnMap = new HashMap<>();
//
//        Map<String, String> userMetadata =
//                s3Client.getObjectMetadata(new GetObjectMetadataRequest(bucketName, objectKey)).getUserMetadata();
//
//        String fileFormat = userMetadata.get("Content-Type");
//        String uploadTime = userMetadata.get("Last-Modified");
//        returnMap.put("File format: ", fileFormat);
//        returnMap.put("Upload time: ", uploadTime);
//        return returnMap;
//
//    }
//
//    // TODO: 5/2/2023 8)
//    @Override
//    public void uploadZip(String bucketName, String key, File file) throws FileNotFoundException {
//
//        // Upload the file to S3
//        s3Util.simpleUploadFile(bucketName, key, file);
//
//        // Extract the files and directories from the zip file and upload them to S3
//
//        InputStream inputStream
//                = new FileInputStream(file);
//        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
//            ZipEntry entry = zis.getNextEntry();
//            while (entry != null) {
//                String entryName = entry.getName();
//                PutObjectRequest putObjectRequest =
//                        new PutObjectRequest(bucketName, entryName, inputStream, new ObjectMetadata());
//
//                if (!entry.isDirectory()) {
//                    // Upload file to S3
//                    uploadFile(putObjectRequest);
//
//                } else {
//                    // Create directory in S3
//                    createDirectory(bucketName, entryName);
//                }
//                zis.closeEntry();
//                entry = zis.getNextEntry();
//            }
//
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//    }
//
//
//    // TODO: 5/2/2023 9)
//    @Override
//    public File downloadDirectoryAsZip(String bucketName, String directoryKey) throws IOException {
//        // Create a new temporary directory to store the downloaded files
//        Path tempDir = Files.createTempDirectory("downloaded-files");
//
//        // Create a new ZIP file to store the downloaded directory
//        int i = directoryKey.split("/").length - 1;
//        File zipFile = File.createTempFile(directoryKey.split("/")[i], ".zip");
//
//        // List all objects in the specified directory
//        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
//        listObjectsRequest.setBucketName(bucketName);
//        listObjectsRequest.setPrefix(directoryKey);
//
//        ListObjectsV2Result listObjectsResponse;
//        do {
//            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
//            for (S3ObjectSummary object : listObjectsResponse.getObjectSummaries()) {
//
//                // Get the object key and file name
//                String objectKey = object.getKey();
//                String fileName = objectKey.substring(directoryKey.length() + 1); // Remove the directory prefix
//
//                // Download the object to the temporary directory
//                Path filePath = Paths.get(tempDir.toString(), fileName);
//                s3Client.getObject(new GetObjectRequest(bucketName, objectKey));
//
//            }
//
//            listObjectsRequest = new ListObjectsV2Request();
//            listObjectsRequest.setBucketName(bucketName);
//            listObjectsRequest.setPrefix(directoryKey);
//
//        } while (listObjectsResponse.isTruncated());
//
//        // Zip the downloaded directory and its contents
//        zipUtil.zipDirectory(tempDir.toFile().toPath(), zipFile.toPath());
//
//        // Delete the temporary directory and its contents
//        FileUtils.deleteDirectory(tempDir.toFile());
//
//        // Return the ZIP file
//        return zipFile;
//    }
//
//    // TODO: 5/1/2023 12)
//    @Override
//    public long getFolderSize(String bucketName, String folderKey) {
//        long size = 0;
//
//        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
//        listObjectsRequest.setBucketName(bucketName);
//        listObjectsRequest.setPrefix(folderKey + "/");
//
//        ListObjectsV2Result listObjectsResponse;
//        do {
//            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
//
//            for (S3ObjectSummary object : listObjectsResponse.getObjectSummaries()) {
//                size += object.getSize();
//            }
//
//            listObjectsRequest = new ListObjectsV2Request();
//            listObjectsResponse.setBucketName(bucketName);
//            listObjectsRequest.setPrefix(folderKey + "/");
//            listObjectsRequest.setContinuationToken(listObjectsResponse.getContinuationToken());
//
//        } while (listObjectsResponse.isTruncated());
//
//        return size;
//    }
//
//    // TODO: 5/1/2023 13)
//    @Override
//    public Date getFolderCreationDate(String bucketName, String folderKey) {
//        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request();
//        listObjectsRequest.setBucketName(bucketName);
//        listObjectsRequest.setPrefix(folderKey + "/");
//
//        ListObjectsV2Result listObjectsResponse;
//        do {
//            listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
//
//            for (S3ObjectSummary objectSummary : listObjectsResponse.getObjectSummaries()) {
//                if (objectSummary.getKey().equals(folderKey + "/")) {
//
//                    return objectSummary.getLastModified();
//                }
//            }
//
//            listObjectsRequest = new ListObjectsV2Request();
//            listObjectsRequest.setBucketName(bucketName);
//            listObjectsRequest.setPrefix(folderKey + "/");
//            listObjectsRequest.setContinuationToken(listObjectsResponse.getContinuationToken());
//
//        } while (listObjectsResponse.isTruncated());
//
//        return null;
//    }
//
//    public boolean isExistsDirectory(String bucket, String key) throws DirectoryNotEmptyException {
//        if (key.contains(".**"))
//            return s3Client.doesObjectExist(bucket, key);
//
//        else throw new DirectoryNotEmptyException(key);
//    }
//
////    private Map<String, String> getMetadata(String bucketName, String ticketId) {
////        HeadObjectRequest request = HeadObjectRequest.builder()
////                .bucket(bucketName)
////                .key(ticketId)
////                .build();
////        HeadObjectResponse response = s3Client.headObject(request);
////        Map<String, String> metadata = response.metadata();
////
////        logger.info(getMetadata(bucketName, ticketId).toString());
////
////        // Check if the ticket is closed
////        if (metadata.containsKey("isTicketClosed") && Boolean.parseBoolean(metadata.get("isTicketClosed")))
////            throw new TicketClosedException("Cannot reply to a closed ticket.");
////        return metadata;
////    }
////    private void changeMetadata(String bucketName, String ticketId, Map<String, String> metadata, boolean isTicketClosed) {
////        metadata.put("isTicketClosed", Boolean.toString(isTicketClosed));
////        ObjectMetadata newMetadata = new ObjectMetadata();
////        newMetadata.setContentLength(0L);
////        newMetadata.setUserMetadata(metadata);
////
////        /* Copy the ticket file to itself with the new metadata */
////        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
////                .copySource(bucketName + "/" + ticketId)
////                .destinationBucket(bucketName)
////                .destinationKey(ticketId)
////                .metadataDirective(MetadataDirective.REPLACE)
////                .metadata(newMetadata.getUserMetadata())
////                .build();
////        s3Client.copyObject(copyRequest);
////    }
////    public void uploadFile(final String bucketName, final String keyName, final Long contentLength,
////            final String contentType, final InputStream value) throws IOException {
////        Map<String, String> metadata = new HashMap<>();
////        metadata.put("ContentLength", String.valueOf(contentLength));
////        metadata.put("ContentType", contentType);
////
////        PutObjectRequest putObjectRequest =new PutObjectRequest(bucketName, keyName, );
////        .builder()
////                .bucket(bucketName)
////                .key(keyName)
////                .metadata(metadata)
////                .build();
////
////        s3Client.putObject(putObjectRequest);
////        logger.info("File uploaded to bucket({}): {}", bucketName, keyName);
////    }

}
