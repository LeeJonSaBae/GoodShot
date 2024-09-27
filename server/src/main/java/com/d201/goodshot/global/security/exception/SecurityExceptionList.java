package com.d201.goodshot.global.security.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityExceptionList {
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED.value(), "접근이 거부되었습니다."),
    ACCESS_DENIED_03(HttpStatus.FORBIDDEN.value(), "권한이 없는 사용자가 접근하려 했습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "잘못된 토큰 서명입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "지원되지 않는 토큰입니다."),
    ILLEGAL_TOKEN(HttpStatus.UNAUTHORIZED.value(), "토큰이 잘못되었습니다.");

    private final int code;
    private final String message;
}
