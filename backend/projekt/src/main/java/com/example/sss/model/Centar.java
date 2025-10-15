package com.example.sss.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "centri")
public class Centar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String ime;

    @Column(nullable = false)
    private String ophis;

    @Column(nullable = false)
    private Date datumKreacije;

    @Column(nullable = false)
    private String adresa;

    @Column(nullable = false)
    private String grad;

    @Column
    private Double rating;

    @Column(nullable = false)
    private boolean active;

    @Column
    private String imagePath;

    @Column
    private String pdfPath;

    @Column(length = 10000)
    private String pdfContent;
}
