package com.d201.goodshot.user.exception;


import static com.d201.goodshot.user.exception.UserExceptionList.EMAIL_NOT_FOUND;

public class EmailNotFoundException extends UserException {
    public EmailNotFoundException() {
        super(EMAIL_NOT_FOUND.getCode(), EMAIL_NOT_FOUND.getMessage());
    }
}
