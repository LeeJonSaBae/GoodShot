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

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwingData {

    private Long id; // swing id
    private MultipartFile swingVideo;
    private List<MultipartFile> swingImages;
    private List<CommentItem> backSwingComments;
    private List<CommentItem> downSwingComments;
    private List<Float> poseSimilarity;
    private String solution;
    private int score;
    private double tempo;
    private boolean likeStatus;
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

}
