package com.d201.goodshot.swing.domain;

import jakarta.persistence.*;

@Entity
public class Swing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] swingVideo;

    @Column(columnDefinition = "JSON")
    private String jointPoint;

    private double backSwingTime;

    private double downSwingTime;

}
