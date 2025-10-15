package com.example.sss.servisi;

import com.example.sss.model.DTO.KorisnikDTO;
import com.example.sss.model.Korisnik;
import com.example.sss.model.Zahtev;

import java.util.List;

public interface KorisnikServis {

    List<Korisnik> getAll();
    Korisnik createUser(Zahtev zahtev);

}
