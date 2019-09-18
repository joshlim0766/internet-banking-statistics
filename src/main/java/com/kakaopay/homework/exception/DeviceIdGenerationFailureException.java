package com.kakaopay.homework.exception;

public class DeviceIdGenerationFailureException extends RuntimeException {
    public DeviceIdGenerationFailureException () {
        super();
    }

    public DeviceIdGenerationFailureException (String message) {
        super(message);
    }

    public DeviceIdGenerationFailureException (String message, Throwable cause) {
        super(message, cause);
    }
}
