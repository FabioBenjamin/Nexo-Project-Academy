package com.nexus.nexus.exceptions;

public class InvalidCredentialException extends RuntimeException{

    public InvalidCredentialException(String message){
        super(message);
    }
}
