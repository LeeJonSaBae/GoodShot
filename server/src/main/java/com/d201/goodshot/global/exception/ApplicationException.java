package com.d201.goodshot.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApplicationException extends RuntimeException {
    private final int code;
    private final String message;

    protected ApplicationException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}