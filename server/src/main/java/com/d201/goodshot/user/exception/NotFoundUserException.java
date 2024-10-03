package com.d201.goodshot.user.exception;


import static com.d201.goodshot.user.exception.UserExceptionList.NOT_FOUND_USER;

public class NotFoundUserException extends UserException{
    public NotFoundUserException() {
        super(NOT_FOUND_USER.getCode(), NOT_FOUND_USER.getMessage());
    }
}