package com.d201.goodshot.user.exception;

import static com.d201.goodshot.user.exception.UserExceptionList.LOGIN_FAIL;

public class LoginFailException extends UserException {
    public LoginFailException() {
        super(
                LOGIN_FAIL.getErrorCode(),
                LOGIN_FAIL.getHttpStatus(),
                LOGIN_FAIL.getMessage()
        );
    }
}
