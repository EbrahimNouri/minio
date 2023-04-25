package com.example.ceph.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.SetBucketPolicyRequest;
import org.springframework.stereotype.Component;

@Component
public class S3Util {

//    @Value("${s3.bucketName}")
    private String bucketName = "debugger";

//    @Value("${s3.quotaBytes}")
    private Long quotaBytes = 10737418240L;

//    @Value("${s3.maxAge}")
//    private int MAX_AGE_DAYS;

    public void setBucketQuota(/*String bucketName,*/ /*long quotaBytes*/) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();


        // TODO: 4/24/2023 change this
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
        s3Client.setBucketPolicy(new SetBucketPolicyRequest(bucketName, policyJson));
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

