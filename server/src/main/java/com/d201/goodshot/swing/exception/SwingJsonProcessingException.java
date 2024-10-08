package com.d201.goodshot.swing.exception;

import static com.d201.goodshot.swing.exception.SwingExceptionList.SWING_JSON_PROCESS_ERROR;

public class SwingJsonProcessingException extends SwingException{
    public SwingJsonProcessingException() {
        super(SWING_JSON_PROCESS_ERROR.getCode(), SWING_JSON_PROCESS_ERROR.getMessage());
    }
}
