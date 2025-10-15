package com.example.sss.kontroleri;

import com.example.sss.model.DTO.CentarDTO;
import com.example.sss.model.Zahtev;
import com.example.sss.model.enumRequestStatus;
import com.example.sss.repozitorijumi.ZahtevRepozitorijum;
import com.example.sss.servisi.TokenUtils;
import com.example.sss.model.DTO.KorisnickiToken;
import com.example.sss.model.DTO.KorisnikDTO;
import com.example.sss.model.DTO.Kredencijali;
import com.example.sss.model.Korisnik;
import com.example.sss.repozitorijumi.KorisnikRepozitorijum;
import com.example.sss.servisi.KorisnikServis;
import com.example.sss.servisi.ZahtevServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.sss.model.enumRole;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("api/korisnici")
public class KorisnikKontroler {

    @Autowired
    KorisnikServis korisnikServis;

    @Autowired
    ZahtevServis zahtevServis;

    @Autowired
    KorisnikRepozitorijum korisnikRepozitorijum;

    @Autowired
    ZahtevRepozitorijum zahtevRepozitorijum;

    TokenUtils tokenUtils = new TokenUtils();

    @PostMapping("prijava")
    public ResponseEntity<KorisnickiToken> generateToken(@RequestBody Kredencijali kredencijali) {

        Korisnik korisnik = korisnikRepozitorijum.findByEmail(kredencijali.getEmail());

        if(korisnik != null){
            if (korisnik.isActive()) {
                if (kredencijali.getPassword().equals(korisnik.getPassword())) {
                    String token = tokenUtils.generateJwtToken(korisnik);
                    return ResponseEntity.ok(new KorisnickiToken(token, String.valueOf(korisnik.getRole())));
                }
                else
                {
                    return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
                }
            }
            else
            {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
        }
        else
        {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("novizahtev")
    public ResponseEntity<KorisnickiToken> stvoriZahtev(@RequestBody @Validated KorisnikDTO noviKorisnik) {

        noviKorisnik.setUloga("KORISNIK");
        for (int m = 0; m < 10; m++) {
            System.out.println("!!!!!!!!!!!!!!!!");
        }
        System.out.println(noviKorisnik.email + noviKorisnik.password + noviKorisnik.firstName + noviKorisnik.lastName + noviKorisnik.brojTelefona + noviKorisnik.adresa);
        Integer zahtev = zahtevServis.createZahtev(noviKorisnik);
        System.out.println(zahtev);

        if(zahtev == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PostMapping("/prihvatizahtev")
    public ResponseEntity<CentarDTO> izmeniCentar(@RequestParam(value = "id", required = true) String id,
                                                  @RequestParam(value = "prihvati", required = true) String prihvati,
                                                  @RequestHeader("authorization") String token) {

        try {
            Integer.valueOf(id);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            Boolean.valueOf(prihvati);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {

        }

        if (email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.ADMIN) {
                    Zahtev zahtev = zahtevRepozitorijum.nadji(Integer.valueOf(id));

                    if (zahtev != null && zahtev.getStatus() == enumRequestStatus.PENDING) {
                        if (Boolean.parseBoolean(prihvati)) {
                            Korisnik korisnik1 = korisnikServis.createUser(zahtev);

                            if (korisnik1 != null ) {
                                zahtevRepozitorijum.promeniStatus("ACCEPTED", zahtev.getId());

                                return new ResponseEntity<>(null, HttpStatus.OK);
                            }

                        }
                        if (!Boolean.parseBoolean(prihvati)) {
                            zahtevRepozitorijum.promeniStatus("REJECTED", zahtev.getId());

                            return new ResponseEntity<>(null, HttpStatus.OK);
                        }
                    }

                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/svizahtevi")
    public ResponseEntity<List<Zahtev>> sviZahtevi(@RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {

        }

        if (email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                if (korisnik.getRole() == enumRole.ADMIN) {
                    List<Zahtev> sviZahtevi = zahtevRepozitorijum.sviZahtevi();

                    return ResponseEntity.ok(sviZahtevi);
                }
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/izmenikorisnika")
    public ResponseEntity<KorisnikDTO> izmenikorisnika(@RequestParam(value = "email", required = false) String emailrias,
                                                       @RequestParam(value = "password", required = false) String password,
                                                       @RequestParam(value = "firstName", required = false) String firstName,
                                                       @RequestParam(value = "lastName", required = false) String lastName,
                                                       @RequestParam(value = "numTel", required = false) String numTel,
                                                       @RequestParam(value = "address", required = false) String address,
                                                       @RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {

        }
        System.out.println("usracu se");

        if (email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                System.out.println("usracu se");
                if (emailrias != null) {
                    korisnikRepozitorijum.izmeniEmail(emailrias, korisnik.getId());
                }
                if (password != null) {
                    korisnikRepozitorijum.izmeniPassword(password, korisnik.getId());
                }
                if (firstName != null) {
                    korisnikRepozitorijum.izmeniIme(firstName, korisnik.getId());
                }
                if (lastName != null) {
                    korisnikRepozitorijum.izmeniPrezime(lastName, korisnik.getId());
                }
                if (numTel != null) {
                    korisnikRepozitorijum.izmeniTelefon(numTel, korisnik.getId());
                }
                if (address != null) {
                    korisnikRepozitorijum.izmeniAdresu(address, korisnik.getId());
                }

                return new ResponseEntity<>(null, HttpStatus.OK);

            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @GetMapping("/ja")
    public ResponseEntity<KorisnikDTO> getKorisnik(@RequestHeader("authorization") String token) {

        String email = null;
        try {
            email = tokenUtils.getClaimsFromToken(token).getSubject();
        } catch (Exception ignored) {

        }

        if (email != null) {
            Korisnik korisnik = korisnikRepozitorijum.findByEmail(email);

            if (korisnik != null && korisnik.isActive()) {
                KorisnikDTO korisnikDTO = new KorisnikDTO();
                korisnikDTO.email = korisnik.getEmail();
                korisnikDTO.password = korisnik.getPassword();
                korisnikDTO.firstName = korisnik.getFirstName();
                korisnikDTO.lastName = korisnik.getLastName();
                korisnikDTO.brojTelefona = korisnik.getNumTel();
                korisnikDTO.adresa = korisnik.getAddress();
                korisnikDTO.createdAt = korisnik.getCreatedAt();
                korisnikDTO.rodjendan = korisnik.getBirthday();

                return ResponseEntity.ok(korisnikDTO);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

}
