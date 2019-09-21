package com.kakaopay.homework.exception;

public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException () {
        super();
    }

    public ContentNotFoundException (String message) {
        super(message);
    }

    public ContentNotFoundException (String message, Throwable cause) {
        super(message, cause);
    }
}
