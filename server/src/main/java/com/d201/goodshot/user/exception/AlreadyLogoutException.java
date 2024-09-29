package com.d201.goodshot.user.exception;

import static com.d201.goodshot.user.exception.UserExceptionList.ALREADY_LOGOUT_ERROR;

public class AlreadyLogoutException extends UserException {
    public AlreadyLogoutException() {
        super(ALREADY_LOGOUT_ERROR.getCode(), ALREADY_LOGOUT_ERROR.getMessage());
    }
}
