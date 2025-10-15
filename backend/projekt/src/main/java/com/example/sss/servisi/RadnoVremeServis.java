package com.example.sss.servisi;

import java.util.List;

public interface RadnoVremeServis {
    List<Integer> nadjiOtvoreneCentre(List<String> ponedeljak, List<String> utorak, List<String> sreda, List<String> cetvrtak, List<String> petak, List<String> subota, List<String> nedelja);
}
