package com.sajayanegar.storage.service.school;


import com.sajayanegar.storage.exception.BucketExistException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Component
public class S3ServiceImpl implements S3Service {

    private final Long NATIONAL_CODE = new Random(10000L).nextLong();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String uuid = UUID.randomUUID().toString();

    @Autowired
    private MinioClient s3Client;

    @Override // TODO: 5/22/2023 tested but cant set quota bucket
//    @SneakyThrows
    public void createBucket(String bucket)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        if (s3Client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build()))
            throw new BucketExistException("Bucket already exists for " + bucket);


        s3Client.makeBucket(MakeBucketArgs.builder().bucket(bucket).objectLock(true).build());

        s3Client.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(bucket)
                // TODO: 5/22/2023 here
                .build());
    }

    // TODO: 5/1/2023 1)
    @Override
    public void uploadFile(String bucketName, String objectKey, MultipartFile file)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        s3Client.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .build());

    }

    // TODO: 5/1/2023 3) tested
    @Override
    public void deleteFile(String bucketName, String objectKey)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        logger.debug("deleting file");

        s3Client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());

    }

    // TODO: 5/1/2023 2) tested
    @Override
    public byte[] readFile(String bucketName, String objectKey)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return s3Client.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey).build()).readAllBytes();

    }

    // TODO: 5/1/2023 7) tested
    @Override
    public void createDirectory(String bucketName, String directoryName)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        // Add a trailing slash to the directory name to indicate that it's a "directory"
        String key = directoryName + "/";

        // Create an empty object with the "directory" key
        byte[] emptyContent = new byte[0];
        InputStream emptyInputStream = new ByteArrayInputStream(emptyContent);
        ObjectMetadata metadata = new ObjectMetadata();
        // TODO: 5/14/2023 set created date to metadata
        PutObjectArgs request = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(key)
                .userMetadata(metadata.userMetadata())
                .stream(emptyInputStream, 0, -1)
                .build();

        // Upload the empty object to S3
        s3Client.putObject(request);
    }

    //  TODO: 5/1/2023 10) tested
    @Override
    public void deleteObjectsInDirectory(String bucketName, String prefix)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
        Iterable<Result<Item>> results = s3Client.listObjects(ListObjectsArgs.builder()
                .prefix(prefix)
                .bucket(bucketName)
                .recursive(true)
                .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            s3Client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
        }
        // حذف دایرکتوری خود دستی
        s3Client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(prefix).build());

    }

    //    @Override
    public void setPassword(String bucket, String objectName, String password)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        SetObjectLockConfigurationArgs setObjectLockConfigurationArgs =
                SetObjectLockConfigurationArgs
                        .builder()
                        .bucket(bucket)
                        .config(new ObjectLockConfiguration())
                        .build();

        s3Client.setObjectLockConfiguration(setObjectLockConfigurationArgs);

        System.out.println("Password applied to object successfully.");
    }


    // TODO: 5/1/2023 14)
    @Override
    public void setBucketQuota(String bucketName, long quotaBytes)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {


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
    }


    // TODO: 4/30/2023 resolve that ↓ 15)
    @Override
    public Map<String, String> showBucketStorageUsage(String bucketName)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        Map<String, String> map = new HashMap<>();
        Iterable<Result<Item>> results = s3Client.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        long usedSize = 0;

        System.out.println("runned list");
        for (Result<Item> result : results) {
            Item item = result.get();
            StatObjectResponse stat = s3Client.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(item.objectName())
                    .build());
            usedSize += stat.size();
        }

        StatObjectResponse bucketStat = s3Client.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object("")
                .build());
        long availableSpace = bucketStat.headers().byteCount();
        map.put("usedSize", String.valueOf(usedSize));
        map.put("availableSpace", String.valueOf(availableSpace));
        map.put("freeSpace", String.valueOf(availableSpace - usedSize));
        return map;
    }

    // TODO: 4/30/2023 must be checked 11) 4) tested
    @Override
    public void backupDirectory(String bucketName,
                                String sourceKeyPrefix,
                                String destinationPath)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {
        List<Result<Item>> resultList = new ArrayList<Result<Item>>();

        // List all objects in the source directory
        Iterable<Result<Item>> results = s3Client.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(sourceKeyPrefix)
                        .recursive(true)
                        .build());

        results.forEach(resultList::add);

        logger.debug(resultList.toString());

        // Iterate over the objects and copy them to the destination directory
        for (Result<Item> result : resultList) {
            Item item = result.get();
            String sourceObject = item.objectName();
            System.out.println(sourceObject);
            String destinationObject = destinationPath + "/" + sourceObject/*.substring(sourceObject.length())*/;

            // Copy the object from source to destination
            System.out.println("Destination object already copied");

            if (item.isDir()) {
                System.out.println("directory");

                backupDirectory(bucketName, sourceObject, destinationObject);

            } else {
                s3Client.copyObject(CopyObjectArgs.builder()
                        .source(CopySource.builder()
                                .bucket(bucketName)
                                .object(sourceObject)
                                .build())
                        .bucket(bucketName)
                        .object(destinationObject)
                        .build());
                logger.debug("file " + sourceObject + " copied to " + destinationObject);
            }
        }

        logger.debug("Files and directories copied successfully.");

    }


    // TODO: 5/2/2023 5) tested
    @Override
    public Long getFileSize(String bucketName, String objectKey)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        StatObjectResponse statObjectResponse = s3Client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectKey).build());
        return statObjectResponse.size();

    }

    // TODO: 5/2/2023 6)
    @Override
    public Map<String, String> getFileFormatAndUploadTime(String bucketName, String objectKey)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        Map<String, String> returnMap = new HashMap<>();

        Map<String, String> userMetadata = s3Client.statObject(StatObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectKey)
                .build()
        ).userMetadata();


        String fileFormat = userMetadata.get("Content-Type");
        String uploadTime = userMetadata.get("Last-Modified");
        returnMap.put("File format: ", fileFormat);
        returnMap.put("Upload time: ", uploadTime);
        return returnMap;

    }

    //    // TODO: 5/2/2023 8)
    @Override
    public void uploadZip(String bucketName, String key, File zipFile)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                String objectName = key + "/" + entryName;

                if (zipEntry.isDirectory()) {
                    // Create the directory in MinIO
                    s3Client.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName + "/")
                                    .stream(zipInputStream, -1, -1) // Empty stream for directories
                                    .build());
                } else {
                    // Upload the file to MinIO
                    s3Client.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .stream(zipInputStream, -1, -1)
                                    .build());
                }

                zipInputStream.closeEntry();
            }
        }
    }


    // TODO: 5/2/2023 9)
    @Override
    public File downloadDirectoryAsZip(String bucketName, String directoryName, String zipObjectName)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        File tempZipFile = null;

        tempZipFile = File.createTempFile("temp", ".zip");
//            tempZipFile.deleteOnExit();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(tempZipFile))) {

            zipMinIODirectoryRecursive(bucketName, directoryName, "", zipOutputStream);

        }

        ObjectWriteResponse objectWriteResponse;
        try (FileInputStream fileInputStream = new FileInputStream(tempZipFile)) {
            objectWriteResponse = s3Client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(zipObjectName)
                    .stream(fileInputStream, fileInputStream.available(), fileInputStream.read())
                    .build());

        }

        return tempZipFile;
    }

    private void zipMinIODirectoryRecursive(String bucketName, String directoryName, String parentPath, ZipOutputStream zipOutputStream)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        Iterable<Result<Item>> objectNames = s3Client.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(directoryName)
                .recursive(false)
                .build());

        for (Result<Item> objectName : objectNames) {
            String entryName = parentPath + objectName;
            String key = objectName.get().objectName();

            if (key.endsWith("/")) {
                // It's a directory, create a zip entry for it
                ZipEntry zipEntry = new ZipEntry(entryName);
                zipOutputStream.putNextEntry(zipEntry);

                // Recursively zip the subdirectory
                zipMinIODirectoryRecursive(bucketName, key, entryName, zipOutputStream);
            } else {
                // It's a file, create a zip entry and write the file contents to the zip
                ZipEntry zipEntry = new ZipEntry(entryName);
                zipOutputStream.putNextEntry(zipEntry);

                InputStream inputStream = s3Client.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(key)
                        .build());
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            }

            zipOutputStream.closeEntry();
        }
    }

    // TODO: 5/1/2023 12) tested
    @Override
    public long getFolderSize(String bucketName, String folderKey)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        long totalSize = 0;

        Iterable<Result<Item>> results = s3Client.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(folderKey)
                        .recursive(true)
                        .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            totalSize += item.size();
        }

        return totalSize;
    }


    // TODO: 5/1/2023 13) tested
    @Override
    public LocalDateTime getFolderCreationDate(String bucketName, String folderKey)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        LocalDateTime folderCreationDate = null;


        Iterable<Result<Item>> results = s3Client.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(folderKey)
                        .recursive(true)
                        .build());

        List<LocalDateTime> dates = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            StatObjectResponse objectStat = s3Client.statObject
                    (StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(item.objectName())
                            .build());

            LocalDateTime creationDate = LocalDateTime.ofInstant(
                    objectStat.lastModified().toInstant(), ZoneOffset.UTC);

            if (folderCreationDate == null || creationDate.isBefore(folderCreationDate)) {
                folderCreationDate = creationDate;
            }
        }

        return folderCreationDate;
    }

    @Override
    public boolean isExistsDirectory(String bucket, String directoryName)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        return s3Client.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(directoryName)
                        .build()).object().endsWith("/");

    }

    @Override
    public void deleteDirectory(String bucket, String key)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        // List all objects in the directory
        Iterable<Result<Item>> results = s3Client.listObjects(ListObjectsArgs.builder()
                .bucket(bucket)
                .prefix(key)
                .recursive(true)
                .build());
        List<DeleteObject> deleteObjects = new ArrayList<>();

        // Collect the object names
        for (Result<Item> result : results) {
            Item item = result.get();
            deleteObjects.add(new DeleteObject(item.objectName()));
        }

        s3Client.removeObjects(RemoveObjectsArgs.builder()
                .bucket(bucket)
                .objects(deleteObjects)
                .build());
    }

    @Override
    public void moveObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey)
            throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {

        s3Client.copyObject(CopyObjectArgs.builder()
                .source(CopySource.builder()
                        .bucket(sourceBucket)
                        .object(sourceKey)
                        .build())
                .bucket(destinationBucket)
                .object(destinationKey)
                .build());

        deleteFile(sourceBucket, sourceKey);
    }

    @Override
    public void copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException {

        s3Client.copyObject(CopyObjectArgs.builder()
                .source(CopySource.builder()
                        .bucket(sourceBucket)
                        .object(sourceKey)
                        .build())
                .bucket(destinationBucket)
                .object(destinationKey)
                .build());
    }

    @Override
    public String testScript() {
        try {
            // Bash command
            String command = "docker exec -it storage_mc_1 mc admin bucket quota TARGET/BUCKET --hard LIMIT";

            // Create the process builder
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);

            // Start the process
            Process process = processBuilder.start();

            // Get the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            return("Command executed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            return "Command failed ";
        }

    }
}
