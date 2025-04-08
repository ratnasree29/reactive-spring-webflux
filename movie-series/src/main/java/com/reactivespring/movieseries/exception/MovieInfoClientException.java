package com.reactivespring.movieseries.exception;

public class MovieInfoClientException extends  RuntimeException{
    private String message;
    public MovieInfoClientException(String message) {
        super(message);
        this.message = message;
    }
}
