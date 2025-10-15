package com.example.sss.servisi.implementacije;

import com.example.sss.model.DTO.KorisnikDTO;
import com.example.sss.model.Korisnik;
import com.example.sss.model.Zahtev;
import com.example.sss.model.enumRole;
import com.example.sss.repozitorijumi.KorisnikRepozitorijum;
import com.example.sss.servisi.KorisnikServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class KorisnikServisImpl implements KorisnikServis {

    @Autowired
    private KorisnikRepozitorijum korisnikRepozitorijum;

    @Override
    public List<Korisnik> getAll() {
        System.out.println(korisnikRepozitorijum.findAll());
        System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFF");
        return korisnikRepozitorijum.findAll();
    }

    @Override
    public Korisnik createUser(Zahtev zahtev) {

        Optional<Korisnik> korisnik = korisnikRepozitorijum.findFirstByEmail(zahtev.getEmail());

        if(korisnik.isPresent()){
            return null;
        }
        korisnik = korisnikRepozitorijum.findFirstByNumTel(zahtev.getNumTel());

        if(korisnik.isPresent()){
            return null;
        }

        System.out.println("GUGUGGUGUGUGU");
        Korisnik novi = new Korisnik();
        novi.setEmail(zahtev.getEmail());
        novi.setPassword(zahtev.getPassword());
        novi.setFirstName(zahtev.getFirstName());
        novi.setLastName(zahtev.getLastName());
        novi.setNumTel(zahtev.getNumTel());
        novi.setAddress(zahtev.getAddress());
        novi.setBirthday(zahtev.getBirthday());
        System.out.println("ASHDASDASDHKJSADHA");
        novi.setRole(enumRole.KORISNIK);
        novi.setActive(true);
        Date date = new Date();
        novi.setCreatedAt(date);

        novi = korisnikRepozitorijum.save(novi);

        return novi;
    }

}
