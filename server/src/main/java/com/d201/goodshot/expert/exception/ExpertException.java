package com.d201.goodshot.expert.exception;

import com.d201.goodshot.global.exception.ApplicationException;

public class ExpertException extends ApplicationException {
    protected ExpertException(int code, String message) {
        super(code, message);
    }
}
