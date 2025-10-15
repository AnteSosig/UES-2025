package com.example.sss.repozitorijumi;

import com.example.sss.model.elasticsearch.CentarDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentarElasticsearchRepository extends ElasticsearchRepository<CentarDocument, Integer> {
    
    List<CentarDocument> findByImeContaining(String ime);
    
    List<CentarDocument> findByOpisContaining(String opis);
    
    List<CentarDocument> findByPdfContentContaining(String pdfContent);
    
    List<CentarDocument> findByImeContainingOrOpisContainingOrPdfContentContaining(
        String ime, String opis, String pdfContent);
}
