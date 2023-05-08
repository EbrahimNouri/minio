package com.sajayanegar.storage.controller;

import com.sajayanegar.storage.exception.DeleteTicketException;
import com.sajayanegar.storage.exception.FileSizeException;
import com.sajayanegar.storage.exception.TicketClosedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalMvcExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(Exception ex, WebRequest request) {
            // 1. status --> BAD REQUEST
            // 2. return exception message to user
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = DeleteTicketException.class)
    public ResponseEntity<String> handleRuntimeException(DeleteTicketException ex, WebRequest request) {
        // 1. status --> BAD REQUEST
        // 2. return exception message to user
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value = FileSizeException.class)
    public ResponseEntity<String> handleRuntimeException(FileSizeException ex, WebRequest request) {
        // 1. status --> BAD REQUEST
        // 2. return exception message to user
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(value = TicketClosedException.class)
    public ResponseEntity<String> handleRuntimeException(TicketClosedException ex, WebRequest request) {
        // 1. status --> BAD REQUEST
        // 2. return exception message to user
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
