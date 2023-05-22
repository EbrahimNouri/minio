package com.sajayanegar.storage.controller;

import com.sajayanegar.storage.service.school.S3Service;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class S3Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private S3Service s3Service;

    @PostMapping("/createBucket/{bucket}")
    public void createBucket(@PathVariable String bucket) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        s3Service.createBucket(bucket);
    }

    @GetMapping("/showBucketStorageUsage/{bucketName}")
    public Map<String, String> showBucketStorageUsage(@PathVariable String bucketName) {


        try {
            return s3Service.showBucketStorageUsage(bucketName);
        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @SneakyThrows
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             String key,
                                             String bucket) {

        logger.debug("Uploading file");
        try {
            s3Service.uploadFile(bucket, key, file);
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @DeleteMapping("/delete") // TODO: 5/22/2023 tested
    public ResponseEntity<String> deleteFile(@RequestParam String bucket,
                                             @RequestParam String object) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        s3Service.deleteFile(bucket, object);
        return new ResponseEntity<>("File deleted successfully", HttpStatus.OK);

    }


    @GetMapping("/download") // TODO: 5/22/2023 tested
    public ResponseEntity<byte[]> downloadFile(@RequestParam String bucket, String key) {

        byte[] data = new byte[0];
        try {
            data = s3Service.readFile(bucket, key);
        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error(e.getMessage(), e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(data.length);
        headers.setContentDispositionFormData("attachment", key);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @PostMapping("/directory") // TODO: 5/22/2023 tested 
    public ResponseEntity<String> createDirectory(@RequestParam String bucket,
                                                  @RequestParam String directory) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // Upload the empty object to S3
        s3Service.createDirectory(bucket, directory);

        return ResponseEntity.ok("Directory created successfully");
    }

    @DeleteMapping("/directory") // TODO: 5/22/2023 tested
    public ResponseEntity<String> deleteObjectsInDirectory(@RequestParam String bucket,
                                                           @RequestParam String directory)
            throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        s3Service.deleteObjectsInDirectory(bucket, directory);

        String message = "All objects in directory deleted successfully";

        logger.debug(message);

        return ResponseEntity.ok(message);
    }

    @PutMapping("/setBucketQuota")
    public ResponseEntity<String> setBucketQuota(String bucketName, long bucketQuota) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        s3Service.setBucketQuota(bucketName, bucketQuota);
        return ResponseEntity.ok("set bucket quota successfully");
    }

    @GetMapping("/backupDir") // TODO: 5/22/2023 tested
    public ResponseEntity<String> getBackupDir(
            @RequestParam String bucketName,
            @RequestParam String source,
            @RequestParam String destination) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        s3Service.backupDirectory(bucketName, source, destination);
        return ResponseEntity.ok("get backup directory successfully");
    }

    @GetMapping("getFileSize") // TODO: 5/22/2023 tested
    public String getFileSize(@RequestParam String bucketName, String objectKey) {
        try {
            return s3Service.getFileSize(bucketName, objectKey) + "byte";
        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @GetMapping("/getFolderCreationDate") // TODO: 5/22/2023 tested
    public String getFolderCreationDate(@RequestParam String bucketName, String folderKey) {
        try {
            return s3Service.getFolderCreationDate(bucketName, folderKey).toString();
        } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @GetMapping("/getFolderSize") // TODO: 5/22/2023 tested
    public Long getFolderSize(@RequestParam String bucket, String key) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return s3Service.getFolderSize(bucket, key);
    }

    @GetMapping("test")
    public String hello() {
        return "hello world";
    }
}
