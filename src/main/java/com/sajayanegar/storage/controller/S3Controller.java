package com.sajayanegar.storage.controller;

import com.sajayanegar.storage.dto.DownloadDto;
import com.sajayanegar.storage.service.school.S3Service;
import io.minio.errors.*;
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

@RestController
@RequestMapping("/api")
public class S3Controller {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private S3Service s3Service;


    @PostMapping("/upload")
    public /*ResponseEntity<String>*/ void uploadFile(@RequestParam("file") MultipartFile file,
                                                      String key,
                                                      String bucket)
            throws IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        logger.debug("Uploading file");

        s3Service.uploadFile(bucket, key, file);
/*
        return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
*/
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("bucketName") String bucketName,
                                             @RequestParam("objectKey") String objectKey) {
        try {
            s3Service.deleteFile(bucketName, objectKey);
            return new ResponseEntity<>("File deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestBody DownloadDto downloadDto)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        byte[] data = s3Service.readFile(downloadDto.getBucket(), downloadDto.getKey());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(data.length);
        headers.setContentDispositionFormData("attachment", downloadDto.getKey());

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @PostMapping("/directory")
    public ResponseEntity<String> createDirectory(@RequestParam("bucketName") String bucketName,
                                                  @RequestParam("directoryName") String directoryName) {


        // Upload the empty object to S3
        s3Service.createDirectory(bucketName, directoryName);

        return ResponseEntity.ok("Directory created successfully");
    }

    @DeleteMapping("/directory")
    public ResponseEntity<String> deleteObjectsInDirectory(@RequestParam("bucketName") String bucketName,
                                                           @RequestParam("directoryName") String directoryName) {

        s3Service.deleteObjectsInDirectory(bucketName, directoryName);

        String message = "All objects in directory deleted successfully";

        logger.debug(message);

        return ResponseEntity.ok(message);
    }

    @PutMapping("/setBucketQuota")
    public ResponseEntity<String> setBucketQuota(String bucketName, long bucketQuota){
        s3Service.setBucketQuota(bucketName, bucketQuota);
        return ResponseEntity.ok("set bucket quota successfully");
    }
}
