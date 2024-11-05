package com.hackathon.bankingapp.exceptions;

public class EmptyFieldException extends RuntimeException {
    public EmptyFieldException(String message) {
        super(message);
    }

    public EmptyFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
