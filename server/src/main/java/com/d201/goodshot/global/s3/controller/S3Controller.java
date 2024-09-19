package com.d201.goodshot.global.s3.controller;

import com.d201.goodshot.global.base.ApiResponse;
import com.d201.goodshot.global.s3.dto.ImageRequest.PresignedUrlRequest;
import com.d201.goodshot.global.s3.dto.ImageResponse.PresignedUrlResponse;
import com.d201.goodshot.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/presigned")
@Tag(name = "presigned") // controller 이름
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping()
    @Operation(summary = "presigned url 발급받기", description = "응답받은 presignedUrl로 PUT method 이용해서 이미지를 업로드하면 imageUrl에 저장되는데, 게시글 작성시 imageUrl을 그대로 보내면 됩니다.")
    public ApiResponse<PresignedUrlResponse> uploadImage(@RequestBody PresignedUrlRequest presignedUrlReq) {
        PresignedUrlResponse response = s3Service.issuePresignedUrl(presignedUrlReq);
        return ApiResponse.created(response);
    }

}
