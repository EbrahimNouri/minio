package com.sajayanegar.storage.exception;

public class TicketClosedException extends RuntimeException {
    public TicketClosedException() {
    }

    public TicketClosedException(String message) {
        super(message);
    }

    public TicketClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketClosedException(Throwable cause) {
        super(cause);
    }

    public TicketClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
