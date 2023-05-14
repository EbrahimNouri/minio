package com.sajayanegar.storage.config;//package com.sajayanegar.storage.config;
//
//import io.minio.MinioClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//
//@Configuration
////@EnableContextInstanceData
//public class S3Config {
//
////    @Autowired
////    private Directories directories;
//
//    //    @Value("${aws.accessKeyId}")
//    private String accessKeyId = "hKUBnff1fgmhcqZr";
//
//    //    @Value("${aws.secretAccessKey}")
//    private String secretAccessKey = "obInxsFGvoCzMWYGmUGcCPYktbGdSpmj";
//
//    //    @Value("${aws.region}")
//    private String region;
//
//    //    @Value("${s3.bucketName}")
//    private String bucketName = "bucket";
//
//    private String endpoint = "127.0.0.1:9000";
//
////    @Value("{$s3.bucketName}")
////    private String bucketName;
//
////    @Value("{$s3.quotaBytes}")
////    private Long quotaBytes;
////
////
////    // TODO: 4/24/2023  https://s3.amazonaws.com/BUCKET_NAME?quota&quota-type=storage&storage-type=standard&bytes=QUOTA_BYTES
////    String endpoint = "https://s3.amazonaws.com/" + bucketName + "?quota&quota-type=storage&storage-type=standard&bytes=" + quotaBytes;
////
////    String auth = awsAccessKey + ":" + awsSecretKey;
////    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
//
//
//    @Bean
//    @Primary
//    public MinioClient minioClient() {
////        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
////
////        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
////                new AwsClientBuilder.EndpointConfiguration(endpoint, null);
//
////        ClientConfiguration clientConfig = new ClientConfiguration();
////        clientConfig.setProtocol(Protocol.HTTP);
//
//        return MinioClient.builder()
//                .endpoint(endpoint)
//                .credentials(accessKeyId, secretAccessKey)
//                .build();
//
//
//
//
//    }
//
////        @Bean
////    @Primary
////    public S3Client s3Client() {
////
////
////        AwsCredentials credentials = AwsBasicCredentials.create("access_key", "secret_key");
////
////        return S3Client.builder()
////                .credentialsProvider(() -> credentials)
////                .region(Region.AWS_ISO_GLOBAL)
//////                .endpointOverride(URI.create(s3EndpointUrl))
//////                .defaultsMode(DefaultsMode.AUTO)
////                .serviceConfiguration(S3Configuration.builder()
////                        .pathStyleAccessEnabled(true)
////                        .build())
////                .build();
////    }
//
//
////    @Bean
////    public Bucket bucket() {
////
////        try (S3Client s3Client1 = S3Client.builder()
////                .region(Region.AWS_ISO_GLOBAL)
////                .defaultsMode(DefaultsMode.AUTO)
////                .credentialsProvider(DefaultCredentialsProvider.create())
////                .build()) {
////
////            s3Client1.createBucket(CreateBucketRequest.builder().bucket("bucket").build());
////
////            // perform S3 operations using s3Client
////        } catch (S3Exception e) {
////            // handle S3 exceptiona
////        }
////
////        return null;
////    }
//
////    @Bean
////    public String bucketName() {
////        return bucketName;
////    }
//
//}

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIoConfig2 {

//    @Value("${s3.endpoint}")
    private String endpoint = "http://localhost:9000";

//    @Value("${s3.accessKey}")
    private String accessKey = "hKUBnff1fgmhcqZr";

//    @Value("${s3.secretKey}")
    private String secretKey = "obInxsFGvoCzMWYGmUGcCPYktbGdSpmj";

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
