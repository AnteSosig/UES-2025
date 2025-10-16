package com.example.sss.servisi;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.sss.model.elasticsearch.CentarDocument;
import com.example.sss.util.SearchQueryParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for building and executing advanced Elasticsearch queries
 * with support for exact phrase matching using quotes
 */
@Service
@Slf4j
public class ElasticsearchQueryService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * Search across ime, opis, and pdfContent fields with support for exact phrase matching.
     * Use quotes to search for exact phrases: "fitness center"
     * Without quotes, terms are matched with fuzzy/partial matching
     *
     * @param queryString The search query (can include quoted phrases)
     * @return List of matching CentarDocument objects
     */
    public List<CentarDocument> searchWithPhraseSupport(String queryString) {
        if (queryString == null || queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        log.debug("Executing search with phrase support: {}", queryString);

        // Parse the query to extract exact phrases and regular terms
        SearchQueryParser.ParsedQuery parsedQuery = SearchQueryParser.parse(queryString);

        if (parsedQuery.isEmpty()) {
            return new ArrayList<>();
        }

        // Build the Elasticsearch query
        Query elasticsearchQuery = buildMultiFieldQuery(parsedQuery);

        // Execute the query
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(elasticsearchQuery)
                .build();

        SearchHits<CentarDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                CentarDocument.class
        );

        List<CentarDocument> results = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        log.debug("Search returned {} results", results.size());
        return results;
    }

    /**
     * Builds a multi-field query that searches across ime, opis, and pdfContent fields
     */
    private Query buildMultiFieldQuery(SearchQueryParser.ParsedQuery parsedQuery) {
        List<Query> shouldClauses = new ArrayList<>();

        // Add exact phrase matches
        for (String phrase : parsedQuery.getExactPhrases()) {
            shouldClauses.addAll(buildPhraseQueries(phrase));
        }

        // Add regular term matches
        for (String term : parsedQuery.getRegularTerms()) {
            shouldClauses.addAll(buildMatchQueries(term));
        }

        // Use a bool query with should clauses (at least one must match)
        return BoolQuery.of(b -> b
                .should(shouldClauses)
                .minimumShouldMatch("1")
        )._toQuery();
    }

    /**
     * Builds match_phrase queries for exact phrase matching across all searchable fields
     */
    private List<Query> buildPhraseQueries(String phrase) {
        List<Query> queries = new ArrayList<>();

        // Search in ime field
        queries.add(MatchPhraseQuery.of(m -> m
                .field("ime")
                .query(phrase)
                .boost(2.0f)  // Boost name matches
        )._toQuery());

        // Search in opis field
        queries.add(MatchPhraseQuery.of(m -> m
                .field("opis")
                .query(phrase)
                .boost(1.5f)  // Boost description matches
        )._toQuery());

        // Search in pdfContent field
        queries.add(MatchPhraseQuery.of(m -> m
                .field("pdfContent")
                .query(phrase)
        )._toQuery());

        return queries;
    }

    /**
     * Builds match queries for fuzzy/partial matching across all searchable fields
     */
    private List<Query> buildMatchQueries(String term) {
        List<Query> queries = new ArrayList<>();

        // Search in ime field with Serbian analyzer
        queries.add(MatchQuery.of(m -> m
                .field("ime")
                .query(term)
                .analyzer("serbian_search_analyzer")
                .boost(2.0f)
        )._toQuery());

        // Search in opis field with Serbian analyzer
        queries.add(MatchQuery.of(m -> m
                .field("opis")
                .query(term)
                .analyzer("serbian_search_analyzer")
                .boost(1.5f)
        )._toQuery());

        // Search in pdfContent field with Serbian analyzer
        queries.add(MatchQuery.of(m -> m
                .field("pdfContent")
                .query(term)
                .analyzer("serbian_search_analyzer")
        )._toQuery());

        return queries;
    }
}
