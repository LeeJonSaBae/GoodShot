package com.d201.goodshot.swing.exception;

import static com.d201.goodshot.swing.exception.SwingExceptionList.SWING_IMAGE_PROCESS_ERROR;

public class SwingImageProcessingException extends SwingException{
    public SwingImageProcessingException() {
        super(SWING_IMAGE_PROCESS_ERROR.getCode(), SWING_IMAGE_PROCESS_ERROR.getMessage());
    }
}
