package com.d201.goodshot.swing.exception;

import static com.d201.goodshot.swing.exception.SwingExceptionList.SWING_VIDEO_PROCESS_ERROR;

public class SwingVideoProcessingException extends SwingException{
    public SwingVideoProcessingException() {
        super(SWING_VIDEO_PROCESS_ERROR.getCode(), SWING_VIDEO_PROCESS_ERROR.getMessage());
    }
}
