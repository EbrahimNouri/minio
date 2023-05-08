package com.sajayanegar.storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.sajayanegar.storage.service.school.S3Service;
import com.sajayanegar.storage.service.school.S3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FirstRunning {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private S3Service s3Service;

    // TODO: 5/8/2023 fill this
    List<String> classes = new ArrayList<String>();

    private final String year = String.valueOf(LocalDateTime.now().getYear());

    private final String[] YEARS_DIR = new String[5];

    private String yearDirectories(int input) {
        switch (input) {
            case 0:
                return year + "/Virtual drive";
            case 1:
                return year + "/Documents";
            case 2:
                return year + "/Educational content";
            case 3:
                return year + "/Home work";
            case 4:
                return year + "/Online class";
        }
        return null;
    }

    private void createDirectory(String bucket, String key) {
        s3Service.createDirectory(bucket, key);
    }

    private String documentDirectories(int numb) {
        switch (numb) {
            case 0:
                return "/Documents/Pre Register";
            case 1:
                return "/Documents/Employee";
            case 2:
                return "/Documents/Students";
        }
        return null;
    }

    private void createDocumentDirectories(String bucket) {
        for (int i = 0; i < 3; i++) {
            createDirectory(bucket, documentDirectories(i));
        }
    }


    public void createDirectory(String bucket) {

        createDirectory(bucket, year);
        for (int i = 0; i < 5; i++) {
            createDirectory(bucket, yearDirectories(i));
            switch (i) {
                case 0:
                    continue;
                case 1:
                    createDocumentDirectories(bucket);
                case 2:
                    createDirectory(bucket, yearDirectories(2));
            }
        }
    }
}
