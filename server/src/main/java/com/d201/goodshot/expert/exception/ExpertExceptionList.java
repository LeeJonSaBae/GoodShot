package com.d201.goodshot.expert.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExpertExceptionList {

    NOT_FOUND_EXPERT(HttpStatus.NOT_FOUND.value(), "존재하지 않는 전문가입니다.");

    private final int code;
    private final String message;

}
