package com.sajayanegar.storage.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIoConfig2 {

//    @Value("${s3.endpoint}")
    private String endpoint = "http://localhost:9000";

//    @Value("${s3.accessKey}")
    private String accessKey = "9f7VLRpfB9IbsUztmuP5";

//    @Value("${s3.secretKey}")
    private String secretKey = "oQqjf2uBkXtJgp71xwwMfRHSZprJvfg5JNy9Sckw";

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

//    @Bean
//    public Account account(){
//
//        AccountConfig accountConfig = new AccountConfig();
//        accountConfig.setUsername(accessKey);
//        accountConfig.setPassword(secretKey);
//        accountConfig.setAuthUrl(endpoint);
//
//        return new AccountFactory(accountConfig).createAccount();
//
//    }
}