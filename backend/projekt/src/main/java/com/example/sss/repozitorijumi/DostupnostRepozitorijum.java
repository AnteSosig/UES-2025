package com.example.sss.repozitorijumi;

import com.example.sss.model.Centar;
import com.example.sss.model.Dostupnost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Repository
public interface DostupnostRepozitorijum extends JpaRepository<Dostupnost, Long> {

    @Query(value = "SELECT * FROM dostupnost WHERE disciplina_id = :id AND active = true", nativeQuery = true)
    List<Dostupnost> nadjiCentreSaDatomDisciplinom(@Param("id") int id);

    @Query(value = "SELECT * FROM dostupnost WHERE centar_id = :id AND active = true", nativeQuery = true)
    List<Dostupnost> nadjiDisciplineCentra(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE dostupnost SET active = false WHERE centar_id = :id", nativeQuery = true)
    void obrisi(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO dostupnost (centar_id, disciplina_id, active) VALUES (:cid, :did, true)", nativeQuery = true)
    int insert(@Param("cid") int cid, @Param("did") int did);

}
