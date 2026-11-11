package com.example.booking.common.exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
