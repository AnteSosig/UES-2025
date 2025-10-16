package com.example.sss.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CentarDTO {

    public Integer id;
    public String ime;
    public String ophis;
    public Date datumKreacije;
    public String adresa;
    public String grad;
    public Double rating;
    public Integer reviewCount;
    public List<RadnoVremeDTO> radnoVremeDTOList;
    public String imagePath;
    public String pdfPath;
    public Boolean hasPdf;
}
