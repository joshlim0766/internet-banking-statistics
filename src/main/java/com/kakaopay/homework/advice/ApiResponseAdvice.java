package com.kakaopay.homework.advice;

import com.kakaopay.homework.controller.dto.ApiErrorResponse;
import com.kakaopay.homework.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ApiResponseAdvice {

    @ExceptionHandler(
            value = {
                    ContentNotFoundException.class,
                    UserNotFoundException.class
            }
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException (final Exception e) {
        log.warn(e.getMessage(), e);

        return new ApiErrorResponse(e.getMessage());
    }

    @ExceptionHandler(
            value = {
                    InvalidTokenException.class,
                    UserAlreadyExistException.class
            }
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequest (final Exception e) {
        log.warn(e.getMessage(), e);

        return new ApiErrorResponse(e.getMessage());
    }

    @ExceptionHandler(
            value = {
                    CsvParseException.class,
                    DeviceIdGenerateException.class,
                    RuntimeException.class
            }
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleInternalServerError (final Exception e) {
        log.warn(e.getMessage(), e);

        return new ApiErrorResponse(e.getMessage());
    }
}
