package com.d201.goodshot.expert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ExpertResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpertItemResponse{
        int pageNo;
        int pageSize;
        private boolean hasNext; // 다음 페이지 여부
        List<ExpertItem> experts;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpertItem {
        String imageUrl;
        String name;
        int expYears; // 경력
        String field; // 분야
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExpertDetailItem{
        String imageUrl;
        String name;
        int expYears;
        List<String> certificates;
    }

}
