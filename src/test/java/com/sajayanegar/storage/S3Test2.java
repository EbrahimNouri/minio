package com.sajayanegar.storage;//package com.example.ceph;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//import java.io.File;
//
//import com.amazonaws.services.s3.model.Bucket;
//import com.example.ceph.util.S3Util;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectResponse;
//
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//public class S3Test2 {
//
//
//    private final S3Util s3Util = new S3Util();
//
//
//    // Create a test file
//    File file = new File("test-file.txt");
//
//    // Create a test bucket name and object key
//    String bucketName = "test-bucket";
//    String objectKey = "test-object-key";
//
//
//    // Create a mock S3Client
////    @Mock
////    S3Client s3Client;
////
////    @Before
////    void beforeEach() {
////        s3Client = mock(S3Client.class);
////    }
//
////    @MockBean
//    private S3Client s3Client;
//
////    @MockBean
//    private Bucket bucket;
//
//
//    @Test
//    public void testSimpleUploadFile() {
//
//        // Create a PutObjectRequest object
//        PutObjectRequest request = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(objectKey)
//                .build();
//
//        // Create a PutObjectResponse object
//        PutObjectResponse response = PutObjectResponse.builder()
//                .eTag("test-etag")
//                .build();
//
//        // Mock the putObject method of the S3Client to return the test response
//        when(s3Client.putObject(request, RequestBody.fromFile(file)))
//                .thenReturn(response);
//
//        // Call the simpleUploadFile method with the mock S3Client
//        s3Util.simpleUploadFile(bucketName, objectKey, file);
//
//        // Verify that the putObject method was called with the correct arguments
//        verify(s3Client).putObject(request, RequestBody.fromFile(file));
//
//        // Assert that the ETag of the response matches the expected value
//        assertEquals("test-etag", response.eTag());
//    }
//}
