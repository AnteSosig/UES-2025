package com.example.sss.repozitorijumi;

import com.example.sss.model.RadnoVreme;
import com.example.sss.model.Rezervacija;
import com.example.sss.model.Zahtev;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface RezervacijaRepozitorijum extends JpaRepository<Rezervacija, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO rezervacije (datum, pocetak, kraj, centar_id, korisnik_id, active) VALUES (:datum, :pocetak, :kraj, :centar, :korisnik, true)", nativeQuery = true)
    int insert(@Param("datum") Date datum, @Param("pocetak") Time pocetak, @Param("kraj") Time kraj, @Param("centar") int centar, @Param("korisnik") int korisnik);

    @Query(value ="SELECT * FROM rezervacije WHERE korisnik_id = :id", nativeQuery = true)
    List<Rezervacija> mojeRezervacije(@Param("id") int id);
}
