package com.example.sss.repozitorijumi;

import com.example.sss.model.Dostupnost;
import com.example.sss.model.RadnoVreme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.List;

@Repository
public interface RadnoVremeRepozitorijum extends JpaRepository<RadnoVreme, Long> {
    @Query(value = "SELECT * FROM radna_vremena WHERE dan_nedelje = :dan AND vreme_otvaranja <= :vremeOtvaranja AND vreme_zatvaranja >= :vremeZatvaranja AND active = true", nativeQuery = true)
    List<RadnoVreme> nadjiCentreZaVremenskiRaspon(@Param("dan") String dan, @Param("vremeOtvaranja") Time vremeOtvaranja, @Param("vremeZatvaranja") Time vremeZatvaranja);

    @Query(value = "SELECT * FROM radna_vremena WHERE centar_id = :id AND dan_nedelje = :dan AND active = true", nativeQuery = true)
    List<RadnoVreme> otvoren(@Param("id") int id, @Param("dan") String dan);

    @Query(value = "SELECT * FROM radna_vremena WHERE centar_id = :id AND active = true", nativeQuery = true)
    List<RadnoVreme> radnaVremenaZaCentar(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO radna_vremena (dan_nedelje, vreme_otvaranja, vreme_zatvaranja, centar_id, active) VALUES (:dan, :otvaranje, :zatvaranje, :cid, true)", nativeQuery = true)
    int novoRadnoVreme(@Param("dan") String dan, @Param("otvaranje") Time otvaranje, @Param("zatvaranje") Time zatvaranje, @Param("cid") int cid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE radna_vremena SET active = false WHERE id = :id", nativeQuery = true)
    void obrisi(@Param("id") int id);

}
