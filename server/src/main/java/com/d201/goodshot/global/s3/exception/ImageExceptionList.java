package com.d201.goodshot.global.s3.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageExceptionList {

    IMAGE_EXTENSION_ERROR("I0001", HttpStatus.BAD_REQUEST, "유효하지 않은 이미지 확장자 입니다.");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

}
