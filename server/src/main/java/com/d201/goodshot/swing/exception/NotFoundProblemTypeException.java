package com.d201.goodshot.swing.exception;


import static com.d201.goodshot.swing.exception.SwingExceptionList.NOT_FOUND_REPORT_COMMENT;

public class NotFoundProblemTypeException extends SwingException {
    public NotFoundProblemTypeException() {
        super(NOT_FOUND_REPORT_COMMENT.getCode(), NOT_FOUND_REPORT_COMMENT.getMessage());
    }
}
