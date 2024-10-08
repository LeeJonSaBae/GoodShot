package com.d201.goodshot.swing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public class SwingRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SwingDataRequest {
        private List<String> codes;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SwingUpdateDataRequest {
        private Boolean likeStatus;
        private String title;
        private String code;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime time;

        private int update; // 1 : 수정, 2 : 삭제 (수정된 레코드, 삭제된 레코드)
    }


}
