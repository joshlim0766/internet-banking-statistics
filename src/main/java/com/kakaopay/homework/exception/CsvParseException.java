package com.kakaopay.homework.exception;

public class CsvParseException extends RuntimeException {
    public CsvParseException () {
        super();
    }

    public CsvParseException (String message) {
        super(message);
    }

    public CsvParseException (String message, Throwable cause) {
        super(message, cause);
    }
}
