package com.d201.goodshot.global.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.d201.goodshot.global.s3.dto.ImageRequest.PresignedUrlRequest;
import com.d201.goodshot.global.s3.dto.ImageResponse.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${image.prefix}")
    private String prefix;

    @Value("${image.folder}")
    private String folder;

    // 발급
    @Transactional(readOnly = true)
    public PresignedUrlResponse issuePresignedUrl(PresignedUrlRequest presignedUrlReq, Long userId, String type, String code){

        String imageName = folder + UUID.randomUUID() + "." + presignedUrlReq.getImageExtension().getUploadExtension();

        if(userId!=null && type!=null && code!=null){
            imageName = folder + userId + "/" + type + "/" + code + "." + presignedUrlReq.getImageExtension().getUploadExtension();
        }

        GeneratePresignedUrlRequest request = generatePresignedUrlRequest(bucket, imageName);
        return PresignedUrlResponse.builder()
                .presignedUrl(amazonS3.generatePresignedUrl(request).toString())
                .imageUrl(prefix + imageName)
                .build();
    }

    // 요청
    private GeneratePresignedUrlRequest generatePresignedUrlRequest(String bucket, String imageName) {
        // 만료 시간 설정
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5; // 5분
        expiration.setTime(expTimeMillis);

        // Pre-Signed Url request 생성
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, imageName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        // request 파라미터 추가
        request.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString());

        return request;
    }

    // S3 객체 제거
    public void deleteObject(String url) {
        // 객체 키 추출
        String objectKey = extractObjectKeyFromUrl(url);

        // 객체 삭제
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, objectKey));
    }

    private String extractObjectKeyFromUrl(String url) {
        // URL 에서 버킷 프리픽스 제거
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return url;
    }

}
