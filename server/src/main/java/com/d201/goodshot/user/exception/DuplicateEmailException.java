package com.d201.goodshot.user.exception;

import static com.d201.goodshot.user.exception.UserExceptionList.DUPLICATE_USER_EMAIL;

public class DuplicateEmailException extends UserException{
    public DuplicateEmailException() {
        super(DUPLICATE_USER_EMAIL.getCode(), DUPLICATE_USER_EMAIL.getMessage());
    }
}
