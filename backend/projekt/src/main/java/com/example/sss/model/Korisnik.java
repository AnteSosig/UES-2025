package com.example.sss.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "korisnici")
public class Korisnik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
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

    @Column
    private String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private enumRole role;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = false)
    private Date birthday;

}
