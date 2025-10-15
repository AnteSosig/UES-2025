package com.example.sss.servisi;

import com.example.sss.model.Centar;
import com.example.sss.model.Dostupnost;

import java.util.List;

public interface DostupnostServis {
    List<Integer> nadjiCentreSaDatimDisciplinama(List<Integer> disciplineIds);
}
