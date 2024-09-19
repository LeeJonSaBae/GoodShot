package com.d201.goodshot.global.s3.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.d201.goodshot.global.s3.exception.ImageExtensionException;

import java.util.Arrays;

public class ImageRequest {

    @AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 생성
    @Getter
    public enum ImageExtension {
        WEBP("webp"),
        PNG("png"),
        JPG("jpg"),
        JPEG("jpeg");

        @JsonValue // JSON 직렬화
        private final String uploadExtension;

        @JsonCreator // JSON 역직렬화
        public static ImageExtension of(String imageExtension) {
            return Arrays.stream(ImageExtension.values())
                    .filter(e->e.uploadExtension.equals(imageExtension))
                    .findAny().orElseThrow(ImageExtensionException::new);
        }
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PresignedUrlReq{
        ImageExtension imageExtension;
    }

}
