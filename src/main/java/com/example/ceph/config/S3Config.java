package com.example.ceph.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;


@Configuration
//@EnableContextInstanceData
public class S3Config {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    @Value("${s3.bucketName}")
    private String bucketName;

//    @Value("{$s3.bucketName}")
//    private String bucketName;

//    @Value("{$s3.quotaBytes}")
//    private Long quotaBytes;
//
//
//    // TODO: 4/24/2023  https://s3.amazonaws.com/BUCKET_NAME?quota&quota-type=storage&storage-type=standard&bytes=QUOTA_BYTES
//    String endpoint = "https://s3.amazonaws.com/" + bucketName + "?quota&quota-type=storage&storage-type=standard&bytes=" + quotaBytes;
//
//    String auth = awsAccessKey + ":" + awsSecretKey;
//    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

//
//    @Bean
//    @Primary
//    public AmazonS3 amazonS3() {
//        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
//
//        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
//                new AwsClientBuilder.EndpointConfiguration(s3EndpointUrl, s3Region);
//
////        ClientConfiguration clientConfig = new ClientConfiguration();
////        clientConfig.setProtocol(Protocol.HTTP);
//
//        return AmazonS3ClientBuilder
//                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//                .withEndpointConfiguration(endpointConfiguration)
//                .build();
//    }

    @Bean
//    @Primary
    public S3Client  s3Client() {


        AwsCredentials credentials = AwsBasicCredentials.create("access_key", "secret_key");

        return S3Client.builder()
//                .credentialsProvider(() -> credentials)
                .region(Region.AWS_ISO_GLOBAL)
//                .endpointOverride(URI.create(s3EndpointUrl))
                .defaultsMode(DefaultsMode.AUTO)
//                .serviceConfiguration(S3Configuration.builder()
//                        .pathStyleAccessEnabled(true)
//                        .build())
                .build();
    }



//    @Bean
//    @Primary
//    public S3Client s3Client() {
//
//
//        AwsCredentials credentials = AwsBasicCredentials.create("access_key", "secret_key");
//
//        return S3Client.builder()
//                .credentialsProvider(() -> credentials)
//                .region(Region.AWS_ISO_GLOBAL)
//                .endpointOverride(URI.create(s3EndpointUrl))
//                .serviceConfiguration(S3Configuration.builder()
//                        .pathStyleAccessEnabled(true)
//                        .build())
//                .build();
//    }

//    @Bean
//    public Bucket bucket() {
//
//        try (S3Client s3Client1 = S3Client.builder()
//                .region(Region.AWS_ISO_GLOBAL)
//                .defaultsMode(DefaultsMode.AUTO)
//                .credentialsProvider(DefaultCredentialsProvider.create())
//                .build()) {
//
//            s3Client1.createBucket(CreateBucketRequest.builder().bucket("bucket").build());
//
//            // perform S3 operations using s3Client
//        } catch (S3Exception e) {
//            // handle S3 exception
//        }
//
//        return null;
//    }

    @Bean
    public String bucketName() {
        return bucketName;
    }

}
