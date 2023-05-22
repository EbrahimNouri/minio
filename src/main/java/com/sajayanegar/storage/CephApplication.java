package com.sajayanegar.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
public class CephApplication {

    //my
    public static void main(String[] args) {

        SpringApplication.run(CephApplication.class, args);
    }
}
