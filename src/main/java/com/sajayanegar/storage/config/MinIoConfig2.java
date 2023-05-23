package com.sajayanegar.storage.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIoConfig2 {

//    @Value("${s3.endpoint}")
    private String endpoint = "http://localhost:9000";

//    @Value("${s3.accessKey}")
    private String accessKey = "hCmPy5JxbWHCkAio61nJ";

//    @Value("${s3.secretKey}")
    private String secretKey = "cWBGx9uXbAjRv20i4FcB3S6agmOTMsq9VxKmaRNm";

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