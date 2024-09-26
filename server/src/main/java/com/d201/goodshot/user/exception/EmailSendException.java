package com.d201.goodshot.user.exception;

import static com.d201.goodshot.user.exception.UserExceptionList.EMAIL_SEND_ERROR;

public class EmailSendException extends UserException{
    public EmailSendException() {
        super(EMAIL_SEND_ERROR.getCode(), EMAIL_SEND_ERROR.getMessage());
    }
}
