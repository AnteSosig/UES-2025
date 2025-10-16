/*
 * EXAMPLE USAGE - Elasticsearch Phrase Matching
 * 
 * This file demonstrates how the new phrase matching feature works.
 * The actual implementation is already integrated into CentarKontroler.java
 * at the /api/centri/search endpoint.
 */

package com.example.sss.examples;

import com.example.sss.servisi.ElasticsearchQueryService;
import com.example.sss.model.elasticsearch.CentarDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Example controller showing how to use the new phrase matching feature.
 * In production, use the existing /api/centri/search endpoint.
 */
@Slf4j
public class SearchExampleController {

    @Autowired
    private ElasticsearchQueryService elasticsearchQueryService;

    /**
     * Example 1: Simple fuzzy search
     * GET /example/search?query=fitness
     * Searches for "fitness" with fuzzy matching
     */
    public List<CentarDocument> exampleSimpleSearch(String query) {
        // query = "fitness"
        return elasticsearchQueryService.searchWithPhraseSupport(query);
    }

    /**
     * Example 2: Exact phrase search
     * GET /example/search?query="fitness centar"
     * Searches for exact phrase "fitness centar"
     */
    public List<CentarDocument> examplePhraseSearch(String query) {
        // query = "\"fitness centar\""
        return elasticsearchQueryService.searchWithPhraseSupport(query);
    }

    /**
     * Example 3: Mixed search
     * GET /example/search?query=beograd "fitness centar" yoga
     * Searches for:
     * - Exact phrase: "fitness centar"
     * - Fuzzy terms: "beograd", "yoga"
     */
    public List<CentarDocument> exampleMixedSearch(String query) {
        // query = "beograd \"fitness centar\" yoga"
        return elasticsearchQueryService.searchWithPhraseSupport(query);
    }

    /**
     * Example 4: Multiple phrases
     * GET /example/search?query="wellness spa" "personal training" beograd
     * Searches for:
     * - Exact phrases: "wellness spa", "personal training"
     * - Fuzzy term: "beograd"
     */
    public List<CentarDocument> exampleMultiplePhrases(String query) {
        // query = "\"wellness spa\" \"personal training\" beograd"
        return elasticsearchQueryService.searchWithPhraseSupport(query);
    }

    /**
     * ACTUAL IMPLEMENTATION IN PRODUCTION:
     * 
     * The feature is already integrated into:
     * - CentarKontroler.java -> /api/centri/search endpoint
     * - CentarServisImpl.java -> searchAll() method
     * 
     * Example API call:
     * 
     * curl -X GET "http://localhost:8080/api/centri/search?query=beograd%20%22fitness%20centar%22" \
     *      -H "Authorization: Bearer YOUR_JWT_TOKEN"
     * 
     * The query parameter will be URL-encoded automatically by the browser/client:
     * - Space: %20
     * - Quote: %22
     * 
     * So: beograd "fitness centar"
     * Becomes: beograd%20%22fitness%20centar%22
     */
}
