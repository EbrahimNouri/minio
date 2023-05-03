package com.example.ceph;


import com.example.ceph.service.S3ServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

//    @Autowired
    S3ServiceImpl s3Service = new S3ServiceImpl();



    @Test
    public void testCreateBucket(){
        String bucketName = "test-bucket";
        s3Service.createBucket(bucketName);
        List<Bucket> buckets = s3Client.listBuckets().buckets();
        boolean bucketExists = false;
        for(Bucket bucket : buckets){
            if(bucket.name().equals(bucketName)){
                bucketExists = true;
                break;
            }
        }
        assertTrue(bucketExists);
    }
}
