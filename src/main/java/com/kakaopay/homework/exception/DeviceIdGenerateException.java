package com.kakaopay.homework.exception;

public class DeviceIdGenerateException extends RuntimeException {
    public DeviceIdGenerateException() {
        super();
    }

    public DeviceIdGenerateException(String message) {
        super(message);
    }

    public DeviceIdGenerateException(String message, Throwable cause) {
        super(message, cause);
    }
}
