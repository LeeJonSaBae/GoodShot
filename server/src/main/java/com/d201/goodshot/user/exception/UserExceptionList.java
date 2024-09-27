package com.d201.goodshot.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserExceptionList {

    DUPLICATE_USER_EMAIL(HttpStatus.CONFLICT.value(), "이미 가입된 이메일입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED.value(), "로그인에 실패했습니다."),
    ALREADY_LOGOUT_ERROR( HttpStatus.BAD_REQUEST.value(), "이미 로그아웃한 회원입니다."),
    INVALID_CREDENTIAL(HttpStatus.BAD_REQUEST.value(), "회원 정보가 올바르지 않습니다."),
    EMAIL_SEND_ERROR(HttpStatus.NOT_FOUND.value(), "이메일 발송에 실패했습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "이메일을 찾을 수 없습니다.");

    private final int code;
    private final String message;

}
