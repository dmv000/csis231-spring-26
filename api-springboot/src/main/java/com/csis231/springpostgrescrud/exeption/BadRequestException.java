package com.csis231.springpostgrescrud.exeption;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
