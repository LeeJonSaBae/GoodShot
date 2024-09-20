package com.d201.goodshot.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserExceptionList {

    DUPLICATE_USER_EMAIL("U001", HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

}
