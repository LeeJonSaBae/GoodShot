package com.d201.goodshot.swing.exception;

import static com.d201.goodshot.swing.exception.SwingExceptionList.NOT_FOUND_SWING;

public class NotFoundSwingException extends SwingException{
    public NotFoundSwingException() {
        super(NOT_FOUND_SWING.getCode(), NOT_FOUND_SWING.getMessage());
    }
}
