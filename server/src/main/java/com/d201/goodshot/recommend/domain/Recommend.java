package com.d201.goodshot.recommend.domain;

import jakarta.persistence.*;

@Entity
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String videoUrl;
    
}
