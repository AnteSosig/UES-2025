package com.example.sss.repozitorijumi;

import com.example.sss.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Repository
public interface KorisnikRepozitorijum extends JpaRepository<Korisnik, Long> {

    Korisnik findByEmail(String email);
    Optional<Korisnik> findFirstByEmail(String email);
    Optional<Korisnik> findFirstByNumTel(String numtel);

    @Modifying
    @Transactional
    @Query(value = "UPDATE korisnici SET email = :email WHERE id = :id", nativeQuery = true)
    void izmeniEmail(@Param("email") String email, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE korisnici SET password = :password WHERE id = :id", nativeQuery = true)
    void izmeniPassword(@Param("password") String password, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE korisnici SET first_name = :name WHERE id = :id", nativeQuery = true)
    void izmeniIme(@Param("name") String name, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE korisnici SET last_name = :name WHERE id = :id", nativeQuery = true)
    void izmeniPrezime(@Param("name") String name, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE korisnici SET num_tel = :name WHERE id = :id", nativeQuery = true)
    void izmeniTelefon(@Param("name") String name, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE korisnici SET address = :name WHERE id = :id", nativeQuery = true)
    void izmeniAdresu(@Param("name") String name, @Param("id") int id);
}
