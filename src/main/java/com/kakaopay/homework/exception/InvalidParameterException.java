package com.kakaopay.homework.exception;

public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException () {
        super();
    }

    public InvalidParameterException (String message) {
        super(message);
    }

    public InvalidParameterException (String message, Exception e) {
        super(message, e);
    }
}
