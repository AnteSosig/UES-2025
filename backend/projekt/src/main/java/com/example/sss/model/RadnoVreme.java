package com.example.sss.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "radna_vremena")
public class RadnoVreme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "centar_id", nullable = false)
    private Centar centar;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private enumDanNedelje danNedelje;

    @Column(name = "vreme_otvaranja", nullable = false)
    private Time vremeOtvaranja;

    @Column(name = "vreme_zatvaranja", nullable = false)
    private Time vremeZatvaranja;

    @Column(nullable = false)
    private boolean active;
}
