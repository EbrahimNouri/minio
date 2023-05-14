package com.sajayanegar.storage.exception;

public class BucketExistException extends RuntimeException{
    public BucketExistException() {
    }

    public BucketExistException(String message) {
        super(message);
    }

    public BucketExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public BucketExistException(Throwable cause) {
        super(cause);
    }

    public BucketExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
