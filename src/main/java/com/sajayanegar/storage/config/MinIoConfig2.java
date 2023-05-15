package com.sajayanegar.storage.config;

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