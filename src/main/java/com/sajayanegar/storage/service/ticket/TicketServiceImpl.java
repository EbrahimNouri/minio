//package com.sajayanegar.storage.service.ticket;
//
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.SdkClientException;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.CopyObjectRequest;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.sajayanegar.storage.exception.TicketClosedException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//@Component
//public class TicketServiceImpl implements TicketService {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private String uuid;
//
//    private final String OPEN_TICKET_PATH = uuid + "/ticketPath/closed";
//    private final String CLOSED_TICKET_PATH = uuid + "/ticketPath/open";
//
//    @Autowired
//    private AmazonS3 s3Client;
//
//    // TODO: 5/7/2023
//    @Override
//    public void saveTicket(String bucketName, String ticketId, String ticketContent) {
//
//        InputStream inputStream = new ByteArrayInputStream(ticketContent.getBytes());
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        Map<String, String> metadata = new HashMap<>();
//        metadata.put("isTicketClosed", "false");
//        metadata.put("ticketId", ticketId);
//        objectMetadata.setUserMetadata(metadata);
//
//        PutObjectRequest request = new PutObjectRequest(bucketName, ticketId, inputStream, objectMetadata);
//
//        s3Client.putObject(request);
//        logger.info("Ticket " + ticketId + " uploaded to bucket " + bucketName);
//    }
//
////    @Override
////    public void replyToTicket(String bucketName, String ticketId, String replyMessage) {
////        // Create a new object in the bucket with the reply message as its content
////        byte[] contentBytes = replyMessage.getBytes(StandardCharsets.UTF_8);
////
////        Map<String, String> metadata = new HashMap<>();
////        ObjectMetadata objectMetadata = new ObjectMetadata();
////
////        InputStream inputStream = new ByteArrayInputStream(replyMessage.getBytes());
////        metadata.put("ticketId", ticketId);
////        objectMetadata.setUserMetadata(metadata);
////
////
////        PutObjectRequest putObjectRequest = new PutObjectRequest(
////                bucketName, ticketId + "/reply.txt", inputStream, objectMetadata);
////
////        s3Client.putObject(putObjectRequest);
////
////        // Update the ticket status to "replied"
////        Map<String, String> metadataMap = new HashMap<>();
////        metadataMap.put("status", "replied");
////        metadataMap.put("lastUpdated", String.valueOf(System.currentTimeMillis()));
////        // TODO: 5/7/2023 user metadata
////
////        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
////                bucketName + "/" + uuid + "/" + ticketId + "/"
////                , ticketId, bucketName, ticketId + "/");
////
////        s3Client.copyObject(copyObjectRequest);
////    }
//
//    // TODO: 5/7/2023 first get object metadata if closed throw exception else do work
//    @Override
//    public void replyToTicket(String bucketName, String ticketId, String replyMessage, Boolean isTicketClosed) {
//        try {
//            // Get the metadata of the ticket file
//
//            ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, ticketId);
//            Optional<String> isTicketClosed2 = Optional.of(metadata.getUserMetadata().get("isTicketClosed"));
//            boolean isTicketClosed1 = Boolean.parseBoolean(isTicketClosed2.orElse("false"));
//
//            // Upload the reply message to S3
//            if (isTicketClosed1)
//                throw new TicketClosedException("this token is closed");
//
//            String replyKey = ticketId + "/" + UUID.randomUUID();
//            InputStream inputStream = new ByteArrayInputStream(replyMessage.getBytes(StandardCharsets.UTF_8));
//            ObjectMetadata replyMetadata = new ObjectMetadata();
//            replyMetadata.setContentLength(replyMessage.getBytes(StandardCharsets.UTF_8).length);
//            replyMetadata.setContentType("text/plain");
//            replyMetadata.setUserMetadata(Collections.singletonMap("isTicketClosed", Boolean.toString(isTicketClosed)));
//            s3Client.putObject(new PutObjectRequest(
//                    bucketName, uuid + "/" + "ticket" + ticketId, inputStream, metadata));
//
//        } catch (SdkClientException e) {
//            // Handle SDK client exceptions
//            logger.error(e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public void closeTicket(String bucketName, String ticketId) {
//
//        try {
//
//            // Get the metadata of the ticket file
//            ObjectMetadata metadata = s3Client.getObjectMetadata(bucketName, ticketId);
//
//            // Set the new metadata for the ticket file
//            Optional<String> isTicketClosed = Optional
//                    .ofNullable(metadata.getUserMetadata().replace("isTicketClosed", "true"));
//
//            metadata.addUserMetadata("newmetadata", "newmetadatavalue");
//
//            CopyObjectRequest request = new CopyObjectRequest(bucketName, ticketId, bucketName, ticketId)
//                    .withNewObjectMetadata(metadata);
//
//            s3Client.copyObject(request);
//
//
//            logger.info("Closing the ticket file " + bucketName + " with metadata " + metadata);
//
//        } catch (AmazonServiceException e) {
//            // Handle Amazon service exceptions
//            logger.error(e.getMessage());
//
//        } catch (SdkClientException e) {
//            // Handle SDK client exceptions
//            logger.error(e.getMessage());
//        }
//    }
//}
