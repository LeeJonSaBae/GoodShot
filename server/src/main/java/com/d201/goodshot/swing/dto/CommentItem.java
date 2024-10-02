package com.d201.goodshot.swing.dto;

import com.d201.goodshot.swing.enums.CommentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentItem {
    private CommentType commentType;
    private String content;
}
