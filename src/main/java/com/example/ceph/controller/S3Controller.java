package com.example.ceph.controller;

import java.io.IOException;

import com.example.ceph.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class S3Controller {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("bucketName") String bucketName,
                                             @RequestParam("objectKey") String objectKey,
                                             @RequestParam("file") MultipartFile file) {

        try {
            s3Service.uploadFile(bucketName, objectKey, file);
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error uploading file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @GetMapping("/download/{objectKey}")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("bucketName") String bucketName,
                                               @PathVariable String objectKey) throws IOException {

        byte[] data = s3Service.readFileFromS3(bucketName, objectKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(data.length);
        headers.setContentDispositionFormData("attachment", objectKey);

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
}
