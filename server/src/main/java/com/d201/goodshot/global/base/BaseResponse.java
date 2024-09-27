package com.d201.goodshot.global.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public class BaseResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    private BaseResponse(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> of(HttpStatus status, String message, T data) {
        return new BaseResponse<>(status, message, data);
    }

    public static <T> BaseResponse<T> ok(T data) {
        return of(OK, "SUCCESS", data);
    }

    public static <T> BaseResponse<T> created(T data) {
        return of(CREATED, "CREATED", data);
    }

}
