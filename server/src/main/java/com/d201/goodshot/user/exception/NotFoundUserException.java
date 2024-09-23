package com.d201.goodshot.user.exception;

import org.springframework.http.HttpStatus;

import static com.d201.goodshot.user.exception.UserExceptionList.NOT_FOUND_USER;

public class NotFoundUserException extends UserException{
    public NotFoundUserException() {
        super(NOT_FOUND_USER.getErrorCode(), NOT_FOUND_USER.getHttpStatus(), NOT_FOUND_USER.getMessage());
    }
}
