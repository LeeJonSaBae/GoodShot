package com.d201.goodshot.swing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class SwingRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SwingDataRequest {

        private Long id; // swing id
        private List<CommentItem> backSwingComments;
        private List<CommentItem> downSwingComments;
        private List<Float> poseSimilarity;
        private String solution;
        private int score;
        private double tempo;
        private boolean likeStatus;
        private String code;
        private String title;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;

    }

}
