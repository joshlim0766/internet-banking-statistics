package com.kakaopay.homework.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException() {
        super();
    }

    public UserAlreadyExistException(String msg) {
        super(msg);
    }

    public UserAlreadyExistException(String msg, Exception e) {
        super(msg, e);
    }
}
