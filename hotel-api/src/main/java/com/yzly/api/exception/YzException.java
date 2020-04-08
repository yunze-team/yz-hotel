package com.yzly.api.exception;

public class YzException extends Exception {
    private String transactionId;

    public YzException(String message) {
        super(message);
    }

    public YzException(String transactionId, String message) {
        super(message);
        this.transactionId = transactionId;
    }
}
