package com.example.sss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "rezervacije")
public class Rezervacija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Date datum;

    @Column(nullable = false)
    private Time pocetak;

    @Column(nullable = false)
    private Time kraj;

    @ManyToOne
    @JoinColumn(name = "centar_id", nullable = false)
    private Centar centar;

    @ManyToOne
    @JoinColumn(name = "korisnik_id", nullable = false)
    private Korisnik korisnik;

    @Column(nullable = false)
    private boolean active;
}
