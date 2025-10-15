package com.example.sss.model.DTO;

import com.example.sss.model.Korisnik;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KorisnikDTO {
    public Integer id;
    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public String brojTelefona;
    public String adresa;
    public String uloga;
    public boolean aktivan;
    public Date createdAt;
    public Date rodjendan;

    public KorisnikDTO(Korisnik kreirani) {
        this.email = kreirani.getEmail();
        this.password = kreirani.getPassword();
        this.firstName = kreirani.getFirstName();
        this.lastName = kreirani.getLastName();
        this.brojTelefona = kreirani.getNumTel();
        this.adresa = kreirani.getAddress();
        this.uloga = String.valueOf(kreirani.getRole());
        this.aktivan = true;
    }
}
