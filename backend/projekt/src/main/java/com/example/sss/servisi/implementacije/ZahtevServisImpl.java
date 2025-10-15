package com.example.sss.servisi.implementacije;

import com.example.sss.model.DTO.KorisnikDTO;
import com.example.sss.model.Korisnik;
import com.example.sss.model.Zahtev;
import com.example.sss.model.enumRequestStatus;
import com.example.sss.model.enumRole;
import com.example.sss.repozitorijumi.KorisnikRepozitorijum;
import com.example.sss.repozitorijumi.ZahtevRepozitorijum;
import com.example.sss.servisi.ZahtevServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZahtevServisImpl implements ZahtevServis {

    @Autowired
    ZahtevRepozitorijum zahtevRepozitorijum;

    @Autowired
    KorisnikRepozitorijum korisnikRepozitorijum;

    public Integer createZahtev(KorisnikDTO korisnikDTO) {

        Optional<Korisnik> korisnik = korisnikRepozitorijum.findFirstByEmail(korisnikDTO.getEmail());

        if(korisnik.isPresent()){
            return null;
        }
        korisnik = korisnikRepozitorijum.findFirstByNumTel(korisnikDTO.getBrojTelefona());

        if(korisnik.isPresent()){
            return null;
        }

        List<Zahtev> zahteviZaMejl = zahtevRepozitorijum.zahteviZaMejl(korisnikDTO.getEmail());
        if (!zahteviZaMejl.isEmpty()) {
            for (Zahtev zahtev : zahteviZaMejl) {
                if (zahtev.getStatus() == enumRequestStatus.ACCEPTED || zahtev.getStatus() == enumRequestStatus.PENDING) {
                    return null;
                }
            }
        }

        List<Zahtev> zahteviZaTelefon = zahtevRepozitorijum.zahteviZaTelefon(korisnikDTO.getBrojTelefona());
        if (!zahteviZaTelefon.isEmpty()) {
            for (Zahtev zahtev : zahteviZaTelefon) {
                if (zahtev.getStatus() == enumRequestStatus.ACCEPTED || zahtev.getStatus() == enumRequestStatus.PENDING) {
                    return null;
                }
            }
        }

        zahtevRepozitorijum.insert(korisnikDTO.email, korisnikDTO.password, korisnikDTO.firstName, korisnikDTO.lastName, korisnikDTO.brojTelefona, korisnikDTO.adresa, korisnikDTO.rodjendan, "PENDING");

        return 0;
    }
}
