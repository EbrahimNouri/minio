//package com.example.ceph;
//
//import com.sajayanegar.storage.util.S3Util;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//
//import java.io.File;
//import java.net.URISyntaxException;
//import java.net.URL;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//class S3ServiceTest2 {
//
//    @Mock
//    private S3Client s3Client;
//
//    @InjectMocks
//    private S3Util s3Util;
//
//
//    @Test
//    void testSimpleUploadFile() {
//        s3Client.createBucket(CreateBucketRequest.builder().bucket("test-bucket").build());
//        // arrange
//        String bucketName = "test-bucket";
//        String objectKey = "test-object-key";
//        File file = mock(File.class);
//
//        // act
//        s3Util.simpleUploadFile(bucketName, objectKey, file);
//
//        // assert
//        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//
//        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
//                .thenReturn(PutObjectResponse.builder().eTag("test-etag").build());
//
//    }
//
//    @Test
//    public void testSimpleUploadFile2() throws URISyntaxException {
//        // Load test file from resources
//        URL resource = getClass().getClassLoader().getResource("testfile.txt");
//        File file = new File(resource.toURI());
//
//        // Set bucket name and object key for test
//        String bucketName = "test-bucket";
//        String objectKey = "testfile.txt";
//
//        // Call the method to upload file
//        s3Util.simpleUploadFile(bucketName, objectKey, file);
//
//        // Check if file is uploaded correctly
//        ListObjectsV2Request listReq = ListObjectsV2Request.builder().bucket(bucketName).prefix(objectKey).build();
//        ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);
//        assertTrue(listRes.hasContents());
//        assertEquals(1, listRes.contents().size());
//        assertEquals(objectKey, listRes.contents().get(0).key());
//
//    }
//}