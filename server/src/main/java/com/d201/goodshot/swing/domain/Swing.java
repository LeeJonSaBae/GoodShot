package com.d201.goodshot.swing.domain;

import com.d201.goodshot.swing.dto.SwingRequest;
import com.d201.goodshot.swing.dto.SwingRequest.SwingUpdateDataItem;
import com.d201.goodshot.swing.dto.SwingRequest.SwingUpdateDataRequest;
import com.d201.goodshot.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.annotation.DateTimeFormat;

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

    private String code; // 고유값

    private Boolean likeStatus; // 즐겨찾기 상태

    @Column(columnDefinition = "JSON")
    private String similarity;

    private String solution;

    private int score;

    private double tempo;

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    @OneToMany(mappedBy = "swing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void updateSwingData(SwingUpdateDataItem swingUpdateDataItem) {
        this.likeStatus = swingUpdateDataItem.getLikeStatus();
        this.time = swingUpdateDataItem.getTime();
        this.title = swingUpdateDataItem.getTitle();
    }

}
