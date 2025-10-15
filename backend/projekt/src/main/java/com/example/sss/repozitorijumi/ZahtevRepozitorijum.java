package com.example.sss.repozitorijumi;

import com.example.sss.model.Korisnik;
import com.example.sss.model.Zahtev;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ZahtevRepozitorijum extends JpaRepository<Zahtev, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO zahtevi (email, password, first_name, last_name, num_tel, address, birthday, status) VALUES (:email, :password, :first_name, :last_name, :num_tel, :address, :rodjendan, :status)", nativeQuery = true)
    int insert(@Param("email") String email, @Param("password") String password, @Param("first_name") String first_name, @Param("last_name") String last_name, @Param("num_tel") String num_tel, @Param("address") String address, @Param("rodjendan") Date rodjendan, @Param("status") String status);

    @Query(value ="SELECT * FROM zahtevi WHERE email = :email", nativeQuery = true)
    Optional<Zahtev> findByEmail(@Param("email") String email);

    @Query(value ="SELECT * FROM zahtevi WHERE num_tel = :numTel", nativeQuery = true)
    Optional<Zahtev> findByNumTel(@Param("numTel") String numTel);

    @Query(value ="SELECT * FROM zahtevi WHERE id = :id", nativeQuery = true)
    Zahtev nadji(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE zahtevi SET status = :status WHERE id = :id", nativeQuery = true)
    void promeniStatus(@Param("status") String status, @Param("id") int id);

    @Query(value ="SELECT * FROM zahtevi WHERE status = 'PENDING'", nativeQuery = true)
    List<Zahtev> sviZahtevi();

    @Query(value ="SELECT * FROM zahtevi WHERE email = :email", nativeQuery = true)
    List<Zahtev> zahteviZaMejl(@Param("email") String email);

    @Query(value ="SELECT * FROM zahtevi WHERE num_tel = :numtel", nativeQuery = true)
    List<Zahtev> zahteviZaTelefon(@Param("numtel") String numtel);
}
