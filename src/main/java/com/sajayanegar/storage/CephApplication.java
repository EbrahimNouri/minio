package com.sajayanegar.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CephApplication {

    @Value("${username}")
    private String value;

    //my
    public static void main(String[] args) {
        SpringApplication.run(CephApplication.class, args);



    }

}
