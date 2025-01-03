package com.d201.goodshot.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ImageResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PresignedUrlResponse{
        private String presignedUrl;
        private String imageUrl;
    }

}
