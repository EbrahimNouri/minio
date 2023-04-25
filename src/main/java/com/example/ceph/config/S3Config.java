package com.example.ceph.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.cloud.aws.context.config.annotation.EnableContextInstanceData;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;


@Component
@EnableContextInstanceData
public class S3Config {

//    @Value("${aws.access.key}")
    private String awsAccessKey = null;

//    @Value("${aws.secret.key}")
    private String awsSecretKey = null;

//    @Value("${s3.endpoint.url}")
    private String s3EndpointUrl = "78.157.35.83:8448";

//    @Value("${s3.region}")
    private String s3Region  = "ap-southwest-2";

//    @Value("{$s3.bucketName}")
//    private String bucketName;

//    @Value("{$s3.quotaBytes}")
//    private Long quotaBytes;


//    // TODO: 4/24/2023  https://s3.amazonaws.com/BUCKET_NAME?quota&quota-type=storage&storage-type=standard&bytes=QUOTA_BYTES
//    String endpoint = "https://s3.amazonaws.com/" + bucketName + "?quota&quota-type=storage&storage-type=standard&bytes=" + quotaBytes;
//
//    String auth = awsAccessKey + ":" + awsSecretKey;
//    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));


    @Primary
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(s3EndpointUrl, s3Region);

//        ClientConfiguration clientConfig = new ClientConfiguration();
//        clientConfig.setProtocol(Protocol.HTTP);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }
}
