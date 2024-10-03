package com.d201.goodshot.swing.domain;

import com.d201.goodshot.swing.dto.SwingData;
import com.d201.goodshot.swing.exception.SwingVideoProcessingException;
import com.d201.goodshot.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Table(name = "swing")
public class Swing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    private byte[] swingVideo;

    private boolean likeStatus; // 즐겨찾기 상태

    @Column(columnDefinition = "JSON")
    private String similarity;

    private String solution;

    private int score;

    private double tempo;

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @OneToMany(mappedBy = "swingImage", cascade = CascadeType.ALL)
    private List<SwingImage> swingImages = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public void updateSwing(SwingData swingData) {
        this.solution = swingData.getSolution();
        this.score = swingData.getScore();
        this.tempo = swingData.getTempo();
        this.likeStatus = swingData.isLikeStatus();
        this.title = swingData.getTitle();
        this.time = swingData.getTime();  // 날짜도 갱신
        this.similarity = swingData.getPoseSimilarity().toString(); // JSON 필드도 갱신

        try {
            this.swingVideo = swingData.getSwingVideo().getBytes();
        } catch (IOException e) {
            throw new SwingVideoProcessingException();
        }
    }

}
