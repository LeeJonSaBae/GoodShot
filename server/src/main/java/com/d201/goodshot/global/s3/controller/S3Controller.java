package com.d201.goodshot.global.s3.controller;

import com.d201.goodshot.global.base.ApiResponse;
import com.d201.goodshot.global.s3.dto.ImageRequest.PresignedUrlRequest;
import com.d201.goodshot.global.s3.dto.ImageResponse.PresignedUrlResponse;
import com.d201.goodshot.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/presigned")
public class S3Controller {

    private final S3Service s3Service;

    public ApiResponse<PresignedUrlResponse> uploadImage(@RequestBody PresignedUrlRequest presignedUrlReq) {
        PresignedUrlResponse response = s3Service.issuePresignedUrl(presignedUrlReq);
        return ApiResponse.created(response);
    }

}
