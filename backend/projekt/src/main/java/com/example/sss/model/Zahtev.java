package com.example.sss.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "zahtevi")
public class Zahtev {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String numTel;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Date birthday;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private enumRequestStatus status;

}
