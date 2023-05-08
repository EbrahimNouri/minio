//package com.sajayanegar.storage;
//
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.sajayanegar.storage.service.school.S3ServiceImpl;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//class S3ServiceTest {
//
//    @Mock
//    private AmazonS3 s3Client;
//
////    @Autowired
//    S3ServiceImpl s3Service = new S3ServiceImpl();
//
//
////
////    @Test
////    public void testCreateBucket(){
////        String bucketName = "test-bucket";
////        s3Service.createBucket(bucketName);
////        List<Bucket> buckets = s3Client.listBuckets().buckets();
////        boolean bucketExists = false;
////        for(Bucket bucket : buckets){
////            if(bucket.name().equals(bucketName)){
////                bucketExists = true;
////                break;
////            }
////        }
////        assertTrue(bucketExists);
////    }
//}
