package com.d201.goodshot.user.exception;

import static com.d201.goodshot.user.exception.UserExceptionList.LOGIN_FAIL;

public class LoginFailException extends UserException {
    public LoginFailException() {
        super(
                LOGIN_FAIL.getCode(),
                LOGIN_FAIL.getMessage()
        );
    }
}
