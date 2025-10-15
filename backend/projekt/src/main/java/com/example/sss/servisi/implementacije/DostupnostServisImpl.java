package com.example.sss.servisi.implementacije;

import com.example.sss.model.Centar;
import com.example.sss.model.Dostupnost;
import com.example.sss.repozitorijumi.CentarRepozitorijum;
import com.example.sss.repozitorijumi.DostupnostRepozitorijum;
import com.example.sss.servisi.CentarServis;
import com.example.sss.servisi.DostupnostServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DostupnostServisImpl implements DostupnostServis {
    @Autowired
    DostupnostRepozitorijum dostupnostRepozitorijum;

    public List<Integer> nadjiCentreSaDatimDisciplinama(List<Integer> disciplineIds){
        if(disciplineIds.isEmpty()) {
            return new ArrayList<>();
        }
        System.out.println(disciplineIds);
        List<Dostupnost> centriZaPrvuDisciplinu = dostupnostRepozitorijum.nadjiCentreSaDatomDisciplinom(disciplineIds.get(0));
        for(Dostupnost dostupnost : centriZaPrvuDisciplinu) {
            System.out.println(dostupnost.getCentar().getIme() + "kenjammmmm3");
        }
        List<Integer> prvaDisciplinaIds = centriZaPrvuDisciplinu.stream()
                .map(dostupnost -> dostupnost.getCentar().getId())
                .collect(Collectors.toList());

        Set<Integer> centriZaSveDiscipline = new HashSet<>(prvaDisciplinaIds);

        for(Integer i : prvaDisciplinaIds) {
            System.out.println(i + "kakammmmmmm7");
        }
        for(Integer i : centriZaSveDiscipline) {
            System.out.println(i);
        }

        for(int i = 1; i < disciplineIds.size(); i++) {
            System.out.println("tronjammmmm4");
            List<Dostupnost> centriZaOstaleDiscipline = dostupnostRepozitorijum.nadjiCentreSaDatomDisciplinom(disciplineIds.get(i));
            List<Integer> ostaleDisciplineIds = centriZaOstaleDiscipline.stream()
                    .map(dostupnost -> dostupnost.getCentar().getId())
                    .collect(Collectors.toList());

            for(Dostupnost dostupnost : centriZaOstaleDiscipline) {
                System.out.println(dostupnost.getCentar().getIme() + "seremmmmm2");
            }

            centriZaSveDiscipline.retainAll(ostaleDisciplineIds);
            for(Integer k : centriZaSveDiscipline) {
                System.out.println(k);
            }

        }

        return new ArrayList<>(centriZaSveDiscipline);
    }
}
