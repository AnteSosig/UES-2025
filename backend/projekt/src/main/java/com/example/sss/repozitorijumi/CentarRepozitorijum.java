package com.example.sss.repozitorijumi;

import com.example.sss.model.Centar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface CentarRepozitorijum extends JpaRepository<Centar, Long> {

    Centar findById (int id);

    @Query(value = "SELECT * FROM centri WHERE grad LIKE :grad AND rating >= :ocenamin AND rating <= :ocenamax AND id IN :ids AND active = true", nativeQuery = true)
    List<Centar> filter(@Param("grad") String grad, @Param("ocenamin") int ocenamin, @Param("ocenamax") int ocenamax, @Param("ids") List<Integer> ids);

    @Query(value = "SELECT * FROM centri WHERE grad LIKE :grad AND rating >= :ocenamin AND rating <= :ocenamax AND active = true", nativeQuery = true)
    List<Centar> filterBezDisciplina(@Param("grad") String grad, @Param("ocenamin") int ocenamin, @Param("ocenamax") int ocenamax);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO centri (ime, ophis, datum_kreacije, adresa, grad, rating, active) VALUES (:ime, :ophis, :datumKreacije, :adresa, :grad, 11, true)", nativeQuery = true)
    int insert(@Param("ime") String ime, @Param("ophis") String ophis, @Param("datumKreacije") LocalDateTime datumKreacije, @Param("adresa") String adresa, @Param("grad") String grad);

    @Modifying
    @Transactional
    @Query(value = "UPDATE centri SET ime = :ime WHERE id = :id", nativeQuery = true)
    void izmeniIme(@Param("ime") String ime, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE centri SET ophis = :ophis WHERE id = :id", nativeQuery = true)
    void izmeniOphis(@Param("ophis") String ophis, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE centri SET adresa = :adresa WHERE id = :id", nativeQuery = true)
    void izmeniAdresu(@Param("adresa") String adresa, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE centri SET grad = :grad WHERE id = :id", nativeQuery = true)
    void izmeniGrad(@Param("grad") String grad, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE centri SET rating = :ocena WHERE id = :id", nativeQuery = true)
    void izmeniOcenu(@Param("ocena") double ocena, @Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE centri SET active = :active WHERE id = :id", nativeQuery = true)
    void obrisi(@Param("active") Boolean active, @Param("id") int id);

    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
    int getLastInsertedId();

}
