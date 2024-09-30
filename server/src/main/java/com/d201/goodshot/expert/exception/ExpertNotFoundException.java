package com.d201.goodshot.expert.exception;

import static com.d201.goodshot.expert.exception.ExpertExceptionList.NOT_FOUND_EXPERT;

public class ExpertNotFoundException extends ExpertException{
    public ExpertNotFoundException() {
        super(NOT_FOUND_EXPERT.getCode(), NOT_FOUND_EXPERT.getMessage());
    }
}
