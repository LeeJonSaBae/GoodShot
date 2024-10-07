package com.d201.goodshot.swing.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SwingExceptionList {

    NOT_FOUND_SWING(HttpStatus.NOT_FOUND.value(), "존재하지 않는 스윙입니다."),
    SWING_JSON_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "JSON 파싱을 처리하는 중에 문제가 발생했습니다.");

    private final int code;
    private final String message;

}
