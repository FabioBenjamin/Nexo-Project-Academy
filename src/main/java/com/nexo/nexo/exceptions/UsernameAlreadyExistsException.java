package com.nexo.nexo.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException{

    public UsernameAlreadyExistsException(String message){
        super(message);
    }
}
