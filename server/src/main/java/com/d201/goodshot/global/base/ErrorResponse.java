package com.d201.goodshot.global.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private LocalDateTime timeStamp;
    private String code;
    private String message;

    public ErrorResponse(String errorCode, String message) {
        this.timeStamp = LocalDateTime.now().withNano(0);
        this.code = errorCode;
        this.message = message;
    }
}