package com.d201.goodshot.swing.dto;

import com.d201.goodshot.global.s3.dto.ImageResponse;
import com.d201.goodshot.global.s3.dto.ImageResponse.PresignedUrlResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public class SwingResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SwingDataResponse {

        private Long id; // swing id
        private List<CommentItem> backSwingComments;
        private List<CommentItem> downSwingComments;
        private String similarity;
        private String solution;
        private int score;
        private double tempo;
        private boolean likeStatus;
        private String title;
        private String code;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SwingCodeResponse {
        private String code;
        private List<String> presignedUrls;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportResponse {
        private String similarity; // 유사도
        private String content; // 내용
        private double score; // 점수
        private String name; // 이름
    }

}
