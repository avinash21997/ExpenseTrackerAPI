package com.example.ExpenseTrackerAPI.exceptions;

public class InvalidRequestException extends RuntimeException{

    public InvalidRequestException(String message) {
        super(message);
    }

}
