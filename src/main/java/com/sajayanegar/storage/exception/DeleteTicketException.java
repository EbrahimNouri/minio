package com.sajayanegar.storage.exception;

public class DeleteTicketException extends RuntimeException {
    public DeleteTicketException() {
    }

    public DeleteTicketException(String message) {
        super(message);
    }

    public DeleteTicketException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeleteTicketException(Throwable cause) {
        super(cause);
    }

    public DeleteTicketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
