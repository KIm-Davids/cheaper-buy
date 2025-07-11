package com.packages.scraperapi.exceptions;

public class NoProductFoundException extends RuntimeException{
    public NoProductFoundException(String message){
        super(message);
    }
}
