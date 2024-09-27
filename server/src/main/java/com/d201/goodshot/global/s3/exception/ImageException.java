package com.d201.goodshot.global.s3.exception;

import com.d201.goodshot.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class ImageException extends ApplicationException {
    protected ImageException(int code, String message) {
        super(code, message);
    }
}
