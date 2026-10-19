package com.nexo.exceptions;

public class InvalidPageNumberException extends RuntimeException {
    public InvalidPageNumberException(String message) {
        super(message);
    }
}
