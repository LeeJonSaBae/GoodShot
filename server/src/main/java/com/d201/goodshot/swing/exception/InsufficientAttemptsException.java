package com.d201.goodshot.swing.exception;

import static com.d201.goodshot.swing.exception.SwingExceptionList.INSUFFICIENT_ATTEMPTS;

public class InsufficientAttemptsException extends SwingException{
    public InsufficientAttemptsException() {
        super(INSUFFICIENT_ATTEMPTS.getCode(), INSUFFICIENT_ATTEMPTS.getMessage());
    }
}
