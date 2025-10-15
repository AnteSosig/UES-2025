package com.example.sss.repozitorijumi;

import com.example.sss.model.Ocena;
import com.example.sss.model.RadnoVreme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OcenaRepozitorijum extends JpaRepository<Ocena, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO ocene (opremljenost, osoblje, higijena, prostorija, centar_id, korisnik_id) VALUES (:opremljenost, :osoblje, :higijena, :prostorija, :centar, :korisnik)", nativeQuery = true)
    int insert(@Param("opremljenost") int opremljenost, @Param("osoblje") int osoblje, @Param("higijena") int higijena, @Param("prostorija") int prostorija, @Param("centar") int centar, @Param("korisnik") int korisnik);

    @Query(value ="SELECT * FROM ocene WHERE centar_id = :id", nativeQuery = true)
    List<Ocena> oceneObjekta(@Param("id") int id);

    @Query(value ="SELECT * FROM ocene WHERE centar_id = :cid AND korisnik_id = :kid", nativeQuery = true)
    Ocena duplikat(@Param("cid") int cid, @Param("kid") int kid);

}
