package com.d201.goodshot.swing.domain;

import com.d201.goodshot.swing.enums.FeedbackType;
import com.d201.goodshot.swing.enums.PoseType;
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
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swing_id")
    private Swing swing;

    private PoseType poseType;

    private FeedbackType feedbackType;

    private String comment;
}
