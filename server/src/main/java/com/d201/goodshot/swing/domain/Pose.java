package com.d201.goodshot.swing.domain;

import jakarta.persistence.*;

@Entity
public class Pose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int code; // 포즈 번호

    private String feedback;

    @Lob
    private byte[] swingImage;

    private double similarity; 

}
