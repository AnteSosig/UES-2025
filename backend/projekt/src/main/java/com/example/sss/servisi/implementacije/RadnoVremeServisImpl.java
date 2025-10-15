package com.example.sss.servisi.implementacije;

import com.example.sss.model.Centar;
import com.example.sss.model.Disciplina;
import com.example.sss.model.RadnoVreme;
import com.example.sss.repozitorijumi.DostupnostRepozitorijum;
import com.example.sss.repozitorijumi.RadnoVremeRepozitorijum;
import com.example.sss.servisi.RadnoVremeServis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RadnoVremeServisImpl implements RadnoVremeServis {
    @Autowired
    RadnoVremeRepozitorijum radnoVremeRepozitorijum;

    public List<Integer> nadjiOtvoreneCentre(List<String> ponedeljak, List<String> utorak, List<String> sreda, List<String> cetvrtak, List<String> petak, List<String> subota, List<String> nedelja){
        if(ponedeljak == null && utorak == null && sreda == null && cetvrtak == null && petak == null && subota == null && nedelja == null) {
            return new ArrayList<>();
        }
        Set<Integer> centri = new HashSet<>();
        if (ponedeljak != null) {
            for (String radnovreme : ponedeljak) {
                String[] sati = radnovreme.split("-");
                List<RadnoVreme> centriZaVremenskiRaspon = radnoVremeRepozitorijum.nadjiCentreZaVremenskiRaspon("ponedeljak", Time.valueOf(sati[0]), Time.valueOf(sati[1]));
                List<Centar> centri1 = centriZaVremenskiRaspon.stream()
                        .map(RadnoVreme::getCentar)
                        .collect(Collectors.toList());
                List<Integer> centarIds = centri1.stream()
                        .map(Centar::getId)
                        .collect(Collectors.toList());
                if (centri.isEmpty()) {
                    for (Integer id : centarIds) {
                        centri.add(id);
                    }
                }
                centri.retainAll(centarIds);

                for(Integer k : centri) {
                    System.out.println(k);
                }
            }
        }

        if (utorak != null) {
            for (String radnovreme : utorak) {
                String[] sati = radnovreme.split("-");
                List<RadnoVreme> centriZaVremenskiRaspon = radnoVremeRepozitorijum.nadjiCentreZaVremenskiRaspon("utorak", Time.valueOf(sati[0]), Time.valueOf(sati[1]));
                List<Centar> centri1 = centriZaVremenskiRaspon.stream()
                        .map(RadnoVreme::getCentar)
                        .collect(Collectors.toList());
                List<Integer> centarIds = centri1.stream()
                        .map(Centar::getId)
                        .collect(Collectors.toList());
                if (centri.isEmpty()) {
                    for (Integer id : centarIds) {
                        centri.add(id);
                    }
                }
                centri.retainAll(centarIds);

                for(Integer k : centri) {
                    System.out.println(k);
                }
            }
        }

        if (sreda != null) {
            for (String radnovreme : sreda) {
                String[] sati = radnovreme.split("-");
                List<RadnoVreme> centriZaVremenskiRaspon = radnoVremeRepozitorijum.nadjiCentreZaVremenskiRaspon("sreda", Time.valueOf(sati[0]), Time.valueOf(sati[1]));
                List<Centar> centri1 = centriZaVremenskiRaspon.stream()
                        .map(RadnoVreme::getCentar)
                        .collect(Collectors.toList());
                List<Integer> centarIds = centri1.stream()
                        .map(Centar::getId)
                        .collect(Collectors.toList());
                if (centri.isEmpty()) {
                    for (Integer id : centarIds) {
                        centri.add(id);
                    }
                }
                centri.retainAll(centarIds);

                for(Integer k : centri) {
                    System.out.println(k);
                }
            }
        }

        if (cetvrtak != null) {
            for (String radnovreme : cetvrtak) {
                String[] sati = radnovreme.split("-");
                List<RadnoVreme> centriZaVremenskiRaspon = radnoVremeRepozitorijum.nadjiCentreZaVremenskiRaspon("cetvrtak", Time.valueOf(sati[0]), Time.valueOf(sati[1]));
                List<Centar> centri1 = centriZaVremenskiRaspon.stream()
                        .map(RadnoVreme::getCentar)
                        .collect(Collectors.toList());
                List<Integer> centarIds = centri1.stream()
                        .map(Centar::getId)
                        .collect(Collectors.toList());
                if (centri.isEmpty()) {
                    for (Integer id : centarIds) {
                        centri.add(id);
                    }
                }
                centri.retainAll(centarIds);

                for(Integer k : centri) {
                    System.out.println(k);
                }
            }
        }

        if (petak != null) {
            for (String radnovreme : petak) {
                String[] sati = radnovreme.split("-");
                List<RadnoVreme> centriZaVremenskiRaspon = radnoVremeRepozitorijum.nadjiCentreZaVremenskiRaspon("petak", Time.valueOf(sati[0]), Time.valueOf(sati[1]));
                List<Centar> centri1 = centriZaVremenskiRaspon.stream()
                        .map(RadnoVreme::getCentar)
                        .collect(Collectors.toList());
                List<Integer> centarIds = centri1.stream()
                        .map(Centar::getId)
                        .collect(Collectors.toList());
                if (centri.isEmpty()) {
                    for (Integer id : centarIds) {
                        centri.add(id);
                    }
                }
                centri.retainAll(centarIds);

                for(Integer k : centri) {
                    System.out.println(k);
                }
            }
        }

        if (subota != null) {
            for (String radnovreme : subota) {
                String[] sati = radnovreme.split("-");
                List<RadnoVreme> centriZaVremenskiRaspon = radnoVremeRepozitorijum.nadjiCentreZaVremenskiRaspon("subota", Time.valueOf(sati[0]), Time.valueOf(sati[1]));
                List<Centar> centri1 = centriZaVremenskiRaspon.stream()
                        .map(RadnoVreme::getCentar)
                        .collect(Collectors.toList());
                List<Integer> centarIds = centri1.stream()
                        .map(Centar::getId)
                        .collect(Collectors.toList());
                if (centri.isEmpty()) {
                    for (Integer id : centarIds) {
                        centri.add(id);
                    }
                }
                centri.retainAll(centarIds);

                for(Integer k : centri) {
                    System.out.println(k);
                }
            }
        }

        if (nedelja != null) {
            for (String radnovreme : nedelja) {
                String[] sati = radnovreme.split("-");
                List<RadnoVreme> centriZaVremenskiRaspon = radnoVremeRepozitorijum.nadjiCentreZaVremenskiRaspon("nedelja", Time.valueOf(sati[0]), Time.valueOf(sati[1]));
                List<Centar> centri1 = centriZaVremenskiRaspon.stream()
                        .map(RadnoVreme::getCentar)
                        .collect(Collectors.toList());
                List<Integer> centarIds = centri1.stream()
                        .map(Centar::getId)
                        .collect(Collectors.toList());
                if (centri.isEmpty()) {
                    for (Integer id : centarIds) {
                        centri.add(id);
                    }
                }
                centri.retainAll(centarIds);

                for(Integer k : centri) {
                    System.out.println(k);
                }
            }
        }

        return new ArrayList<>(centri);
    }
}
