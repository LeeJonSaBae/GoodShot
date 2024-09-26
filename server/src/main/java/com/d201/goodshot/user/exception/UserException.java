package com.d201.goodshot.user.exception;

import com.d201.goodshot.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserException extends ApplicationException {
    protected UserException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}
