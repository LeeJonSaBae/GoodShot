package com.d201.goodshot.swing.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Swing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
