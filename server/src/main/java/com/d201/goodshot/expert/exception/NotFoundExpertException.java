package com.d201.goodshot.expert.exception;

import static com.d201.goodshot.expert.exception.ExpertExceptionList.NOT_FOUND_EXPERT;

public class NotFoundExpertException extends ExpertException{
    public NotFoundExpertException() {
        super(NOT_FOUND_EXPERT.getCode(), NOT_FOUND_EXPERT.getMessage());
    }
}
