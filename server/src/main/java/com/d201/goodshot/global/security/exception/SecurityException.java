package com.d201.goodshot.global.security.exception;

import com.d201.goodshot.global.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class SecurityException extends ApplicationException {
    protected SecurityException(int code, String message) {
        super(code, message);
    }
}
