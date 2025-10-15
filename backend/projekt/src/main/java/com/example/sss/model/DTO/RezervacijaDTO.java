package com.example.sss.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RezervacijaDTO {
    public Integer id;
    public Date datum;
    public Time pocetak;
    public Time kraj;
    public int centar;
    public int korisnik;
    public boolean active;
}
