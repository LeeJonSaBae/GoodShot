package com.d201.goodshot.global.security.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityExceptionList {
    INVALID_TOKEN("S0001", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ACCESS_DENIED("S0002", HttpStatus.UNAUTHORIZED, "접근이 거부되었습니다."),
    ACCESS_DENIED_03("S0003", HttpStatus.FORBIDDEN, "권한이 없는 사용자가 접근하려 했습니다."),
    EXPIRED_TOKEN("S0004", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    MALFORMED_TOKEN("S0005", HttpStatus.UNAUTHORIZED, "잘못된 토큰 서명입니다."),
    UNSUPPORTED_TOKEN("S0006", HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰입니다."),
    ILLEGAL_TOKEN("S0007", HttpStatus.UNAUTHORIZED, "토큰이 잘못되었습니다.");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;
}
