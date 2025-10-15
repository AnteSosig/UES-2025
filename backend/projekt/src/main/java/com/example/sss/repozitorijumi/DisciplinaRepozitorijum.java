package com.example.sss.repozitorijumi;

import com.example.sss.model.Centar;
import com.example.sss.model.Disciplina;
import com.example.sss.model.Dostupnost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Repository
public interface DisciplinaRepozitorijum extends JpaRepository<Disciplina, Long> {

    @Query(value = "SELECT * FROM discipline WHERE ime IN :imena", nativeQuery = true)
    List<Disciplina> nadjiDiscipline(@Param("imena") List<String> imena);

    @Query(value = "SELECT * FROM discipline", nativeQuery = true)
    List<Disciplina> sveDiscipline();

}
