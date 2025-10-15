package com.example.sss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ocene")
public class Ocena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    public int opremljenost;

    @Column(nullable = false)
    public int osoblje;

    @Column(nullable = false)
    public int higijena;

    @Column(nullable = false)
    public int prostorija;

    @ManyToOne
    @JoinColumn(name = "centar_id", nullable = false)
    private Centar centar;

    @ManyToOne
    @JoinColumn(name = "korisnik_id", nullable = false)
    private Korisnik korisnik;
}
