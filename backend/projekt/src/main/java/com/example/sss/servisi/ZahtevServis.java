package com.example.sss.servisi;

import com.example.sss.model.DTO.KorisnikDTO;
import com.example.sss.model.Korisnik;
import com.example.sss.model.Zahtev;

public interface ZahtevServis {
    Integer createZahtev(KorisnikDTO korisnikDTO);
}
