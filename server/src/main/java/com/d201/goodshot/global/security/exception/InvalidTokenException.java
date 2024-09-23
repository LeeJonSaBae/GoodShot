package com.d201.goodshot.global.security.exception;

import static com.d201.goodshot.global.security.exception.SecurityExceptionList.INVALID_TOKEN;

public class InvalidTokenException extends SecurityException {
    public InvalidTokenException() {
        super(INVALID_TOKEN.getErrorCode(), INVALID_TOKEN.getHttpStatus(), INVALID_TOKEN.getMessage());
    }
}
