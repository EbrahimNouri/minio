package com.example.ceph.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.CopyObjectResult;


import java.util.List;

@Component
public class S3Util {

    @Autowired
    private S3Client s3Client;

    //    @Value("${s3.quotaBytes}")
    private final Long quotaBytes = 10737418240L;

//    @Value("${s3.maxAge}")
//    private int MAX_AGE_DAYS;

    public void setBucketQuota(/*String bucketName,*/ /*long quotaBytes*/) {



        // TODO: 4/24/2023 change this
        //    @Value("${s3.bucketName}")
        String bucketName = "debugger";
        String policyJson = "{" +
                "\"Version\":\"2012-10-17\"," +
                "\"Statement\":[" +
                "   {" +
                "       \"Sid\":\"DenyGreaterThanQuota\"," +
                "       \"Effect\":\"Deny\"," +
                "       \"Principal\":\"*\"," +
                "       \"Action\":\"s3:PutObject\"," +
                "       \"Resource\":\"arn:aws:s3:::" + bucketName + "/*\"," +
                "       \"Condition\":{" +
                "           \"NumericGreaterThan\":{\"s3:objectSize\":\"" + quotaBytes + "\"}" +
                "       }" +
                "   }" +
                "]" +
                "}";

//        String policyJson1 = String.format("""
//                {
//                   "Version":"2012-10-17",
//                   "Statement":[
//                      {
//                         "Sid":"DenyGreaterThanQuota",
//                         "Effect":"Deny",
//                         "Principal":"*",
//                         "Action":"s3:PutObject",
//                         "Resource":"arn:aws:s3:::%s/*",
//                         "Condition":{
//                            "NumericGreaterThan":{"s3:objectSize":"%d"}
//                         }
//                      }
//                   ]
//                }
//                """, bucketName, quotaBytes);
//
        s3Client.createBucket(CreateBucketRequest.builder().bucket("first").build());
    }

//    public void autoRemoveObject(String DIR_PATH){
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//
//        executorService.scheduleAtFixedRate(() -> {
//            File directory = new File(DIR_PATH);
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.DAY_OF_MONTH, -MAX_AGE_DAYS);
//            long cutoff = cal.getTimeInMillis();
//
//            File[] files = directory.listFiles();
//            for (File file : files) {
//                if (file.lastModified() < cutoff) {
//                    file.delete();
//                }
//            }
//        }, 0, 1, TimeUnit.HOURS);
//    }



}

