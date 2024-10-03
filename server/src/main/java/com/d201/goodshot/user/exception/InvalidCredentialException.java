package com.d201.goodshot.user.exception;

import static com.d201.goodshot.user.exception.UserExceptionList.INVALID_CREDENTIAL;

public class InvalidCredentialException extends UserException{
    public InvalidCredentialException() {
        super(INVALID_CREDENTIAL.getCode(), INVALID_CREDENTIAL.getMessage());
    }
}