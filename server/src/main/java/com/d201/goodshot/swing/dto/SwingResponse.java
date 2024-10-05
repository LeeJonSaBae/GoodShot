package com.d201.goodshot.swing.dto;

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
        private byte[] swingVideo; // 비디오 파일을 byte[]로 반환
        private List<byte[]> swingImages; // 이미지 파일을 byte[]로 반환
        private List<CommentItem> backSwingComments;
        private List<CommentItem> downSwingComments;
        private String poseSimilarity;
        private String solution;
        private int score;
        private double tempo;
        private boolean likeStatus;
        private String title;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportResponse {
        private String poseSimilarity; // 유사도
        private String content; // 내용
        private double score; // 점수
        private String name; // 이름
    }

}
