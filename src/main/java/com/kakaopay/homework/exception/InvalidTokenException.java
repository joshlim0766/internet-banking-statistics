package com.kakaopay.homework.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException () {
        super();
    }

    public InvalidTokenException (String msg) {
        super(msg);
    }

    public InvalidTokenException (String msg, Exception e) {
        super(msg, e);
    }
}
