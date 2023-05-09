package com.sajayanegar.storage.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
//@EnableContextInstanceData
public class S3Config {

//    @Autowired
//    private Directories directories;

//    @Value("${aws.accessKeyId}")
    private String accessKeyId = "hKUBnff1fgmhcqZr";

//    @Value("${aws.secretAccessKey}")
    private String secretAccessKey = "obInxsFGvoCzMWYGmUGcCPYktbGdSpmj";

//    @Value("${aws.region}")
    private String region = String.valueOf(Region.ME_UAE);

//    @Value("${s3.bucketName}")
    private String bucketName;

    private String endpoint = "127.0.0.1:9000";

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


    @Bean
    @Primary
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(endpoint, region);

//        ClientConfiguration clientConfig = new ClientConfiguration();
//        clientConfig.setProtocol(Protocol.HTTP);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

//        @Bean
//    @Primary
//    public S3Client s3Client() {
//
//
//        AwsCredentials credentials = AwsBasicCredentials.create("access_key", "secret_key");
//
//        return S3Client.builder()
//                .credentialsProvider(() -> credentials)
//                .region(Region.AWS_ISO_GLOBAL)
////                .endpointOverride(URI.create(s3EndpointUrl))
////                .defaultsMode(DefaultsMode.AUTO)
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
