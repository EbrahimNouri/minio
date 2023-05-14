//package com.sajayanegar.storage.service;
//
//import com.sajayanegar.storage.service.school.S3Service;
//import io.minio.MinioClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class FirstRunning {
//
//    @Autowired
//    private MinioClient s3Client;
//
//    @Autowired
//    private S3Service s3Service;
//
//    // TODO: 5/8/2023 fill this
//    List<String> classesSchool = new ArrayList<>();
//    List<String> courses = new ArrayList<>();
//
//    private final String year = String.valueOf(LocalDateTime.now().getYear());
//
//    private String yearDirectories(int input) {
//        switch (input) {
//            case 0:
//                return year + "/Virtual drive";
//            case 1:
//                return year + "/Documents";
//            case 2:
//                return year + "/Educational content";
//            case 3:
//                return year + "/Home work";
//            case 4:
//                return year + "/Online class";
//        }
//        return null;
//    }
//
//    // TODO: 5/9/2023
////    public void createClassesOnlineDirectoires();
//
////    private void homeWorkDirectories(String bucket, int input) {
////
////        addClassDirectory(bucket, input);
////
////    }
//
////    private void creatCourseDirectories(String bucket){
////        for (String cours : courses) {
////            for (String s : classesSchool) {
////                createDirectory(bucket, year + "/" + s + "/" + cours);
////            }
////        }
////    }
//
////    private void addClassDirectory(String bucket, int input) {
////        for (int i = 1; i < classesSchool.size(); i++) {
////            createDirectory(bucket, year + yearDirectories(input) + "/" + classesSchool.indexOf(i));
////        }
////    }
//
////    private void createDirectory(String bucket, String key) {
////        s3Service.createDirectory(bucket, key);
////    }
//
//    private String documentDirectories(int numb) {
//        switch (numb) {
//            case 0:
//                return "/Documents/Pre Register";
//            case 1:
//                return "/Documents/Employee";
//            case 2:
//                return "/Documents/Students";
//        }
//        return null;
//    }
//
////    private void createDocumentDirectories(String bucket) {
////        for (int i = 0; i < 3; i++) {
////            createDirectory(bucket, documentDirectories(i));
////        }
////    }
//
////
////    public void createRootDirectory(String bucket) {
////
////        createDirectory(bucket, year);
////        for (int i = 0; i < 5; i++) {
////            createDirectory(bucket, yearDirectories(i));
////
////            switch (i) {
////                case 0:
////                    continue;
////                case 1:
////                    createDocumentDirectories(bucket);
////                case 2:
////                    createDirectory(bucket, yearDirectories(2));
////            }
////        }
////    }
//}
