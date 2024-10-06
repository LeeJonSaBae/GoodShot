package com.d201.goodshot.swing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SwingResponse {

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
