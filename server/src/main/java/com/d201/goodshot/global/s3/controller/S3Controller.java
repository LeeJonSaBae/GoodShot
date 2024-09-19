package com.d201.goodshot.global.s3.controller;

import com.d201.goodshot.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/presigned")
public class S3Controller {

    private final S3Service s3Service;



}
