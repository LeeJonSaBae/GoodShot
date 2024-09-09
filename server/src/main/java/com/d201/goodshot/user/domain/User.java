package com.d201.goodshot.user.domain;

import com.d201.goodshot.user.enums.Role;
import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String profileUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean exit;

}
