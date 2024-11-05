package com.hackathon.bankingapp.exceptions;

public class InvalidIdentifierFormatException extends RuntimeException {
    public InvalidIdentifierFormatException(String message) {
        super(message);
    }

    public InvalidIdentifierFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
