package com.d201.goodshot.swing.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SwingExceptionList {

    NOT_FOUND_SWING(HttpStatus.NOT_FOUND.value(), "존재하지 않는 스윙입니다."),
    NOT_FOUND_REPORT_COMMENT(HttpStatus.NOT_FOUND.value(), "코멘트에 맞는 ENUM 값을 찾을 수 없습니다."),
    SWING_JSON_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "JSON 파싱을 처리하는 중에 문제가 발생했습니다."),
    INSUFFICIENT_ATTEMPTS(HttpStatus.NO_CONTENT.value(), "스윙 시도 횟수가 부족합니다. 15회 이상이어야 종합 리포트 생성이 가능합니다.");

    private final int code;
    private final String message;

}
