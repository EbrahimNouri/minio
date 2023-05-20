package com.sajayanegar.storage.config;

import io.minio.MinioClient;
import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.model.Account;
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

    @Bean
    public Account rgw(){

        AccountConfig config = new AccountConfig();
        config.setUsername("access-key");
        config.setPassword("secret-key");
        config.setAuthUrl("http://localhost:9000");

        return new AccountFactory(config).createAccount();
    }
}