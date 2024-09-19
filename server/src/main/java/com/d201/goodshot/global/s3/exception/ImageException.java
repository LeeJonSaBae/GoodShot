package com.d201.goodshot.global.s3.exception;

import com.d201.goodshot.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class ImageException extends ApplicationException {
    protected ImageException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
