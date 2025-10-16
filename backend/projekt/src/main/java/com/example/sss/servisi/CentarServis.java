package com.example.sss.servisi;

import com.example.sss.model.Centar;
import com.example.sss.model.elasticsearch.CentarDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface CentarServis {

    List<Centar> getAll();

    Centar save(Centar centar);

    Centar uploadFiles(Integer centarId, MultipartFile image, MultipartFile pdf);

    void indexCentar(Centar centar);

    void indexCentar(Centar centar, String pdfContent);

    void indexAllCentri();

    List<CentarDocument> searchByNaziv(String naziv);

    List<CentarDocument> searchByOpis(String opis);

    List<CentarDocument> searchByPdfContent(String content);

    List<CentarDocument> searchAll(String query);

    InputStream downloadPdf(Integer centarId);

    InputStream downloadImage(Integer centarId);

}
