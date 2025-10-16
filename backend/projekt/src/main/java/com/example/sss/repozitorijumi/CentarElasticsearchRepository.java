package com.example.sss.repozitorijumi;

import com.example.sss.model.elasticsearch.CentarDocument;
import org.springframework.data.elasticsearch.annotations.Query;
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
    
    // Custom query for multi-field search with n-gram analyzer for partial matching
    // Searches in ime (name), opis (description), and pdfContent (PDF text)
    @Query("{\"bool\": {\"should\": [{\"match\": {\"ime\": {\"query\": \"?0\", \"analyzer\": \"serbian_search_analyzer\"}}}, {\"match\": {\"opis\": {\"query\": \"?0\", \"analyzer\": \"serbian_search_analyzer\"}}}, {\"match\": {\"pdfContent\": {\"query\": \"?0\", \"analyzer\": \"serbian_search_analyzer\"}}}], \"minimum_should_match\": 1}}")
    List<CentarDocument> searchByQuery(String query);
}
