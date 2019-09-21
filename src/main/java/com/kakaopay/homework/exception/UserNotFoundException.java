package com.kakaopay.homework.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException () {
        super();
    }

    public UserNotFoundException (String msg) {
        super(msg);
    }

    public UserNotFoundException (String msg, Exception e) {
        super(msg, e);
    }
}
