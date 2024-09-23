package com.d201.goodshot.swing.domain;

import com.d201.goodshot.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

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

    @Column(columnDefinition = "JSON")
    private String jointPoint;

    private double backSwingTime;

    private double downSwingTime;

    private boolean likeStatus; // 즐겨찾기 상태 

}
