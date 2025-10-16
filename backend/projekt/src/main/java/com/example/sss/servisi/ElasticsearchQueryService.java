package com.example.sss.servisi;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
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
            log.info("Empty query string, returning empty results");
            return new ArrayList<>();
        }

        log.info("Executing search with phrase support: {}", queryString);

        // Parse the query to extract exact phrases, regular terms, and range queries
        SearchQueryParser.ParsedQuery parsedQuery = SearchQueryParser.parse(queryString);
        log.info("Parsed query - Exact phrases: {}, Regular terms: {}, Range queries: {}", 
                parsedQuery.getExactPhrases(), parsedQuery.getRegularTerms(), parsedQuery.getRangeQueries());

        if (parsedQuery.isEmpty()) {
            log.info("Parsed query is empty, returning empty results");
            return new ArrayList<>();
        }

        // Build the Elasticsearch query
        Query elasticsearchQuery = buildMultiFieldQuery(parsedQuery);
        log.info("Built Elasticsearch query");

        // Execute the query
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(elasticsearchQuery)
                .build();

        log.info("Executing Elasticsearch search query");
        SearchHits<CentarDocument> searchHits = elasticsearchOperations.search(
                searchQuery,
                CentarDocument.class
        );

        List<CentarDocument> results = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        log.info("Search returned {} results for query: {}", results.size(), queryString);
        
        // Debug: Log each result with its score
        searchHits.getSearchHits().forEach(hit -> {
            log.info("  Result: ID={}, Score={}, Name={}", 
                    hit.getContent().getId(), 
                    hit.getScore(), 
                    hit.getContent().getIme());
        });
        
        return results;
    }

    /**
     * Builds a multi-field query that searches across ime, opis, and pdfContent fields,
     * and supports range queries for numeric fields like reviewCount
     */
    private Query buildMultiFieldQuery(SearchQueryParser.ParsedQuery parsedQuery) {
        List<Query> shouldClauses = new ArrayList<>();
        List<Query> mustClauses = new ArrayList<>();

        log.debug("Building multi-field query:");
        log.debug("  - Exact phrases: {}", parsedQuery.getExactPhrases().size());
        log.debug("  - Regular terms: {}", parsedQuery.getRegularTerms().size());
        log.debug("  - Range queries: {}", parsedQuery.getRangeQueries().size());

        // Add exact phrase matches
        for (String phrase : parsedQuery.getExactPhrases()) {
            shouldClauses.addAll(buildPhraseQueries(phrase));
        }

        // Add regular term matches
        for (String term : parsedQuery.getRegularTerms()) {
            shouldClauses.addAll(buildMatchQueries(term));
        }

        // Add range queries as filter clauses
        for (SearchQueryParser.RangeQuery rangeQuery : parsedQuery.getRangeQueries()) {
            log.debug("  - Adding range query: field={}, min={}, max={}", 
                    rangeQuery.getFieldName(), rangeQuery.getMinValue(), rangeQuery.getMaxValue());
            mustClauses.add(buildRangeQuery(rangeQuery));
        }

        // Build the final bool query
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        
        // If we have text search clauses, add them as should with minimum match
        if (!shouldClauses.isEmpty()) {
            boolBuilder.should(shouldClauses).minimumShouldMatch("1");
        }
        
        // Add range filters as must clauses (filters)
        if (!mustClauses.isEmpty()) {
            boolBuilder.filter(mustClauses);
        }
        
        // If there are no text searches, match all documents (filters will apply)
        if (shouldClauses.isEmpty()) {
            // When only filters exist, we need a match_all query
            return BoolQuery.of(b -> b
                .must(Query.of(q -> q.matchAll(m -> m)))
                .filter(mustClauses)
            )._toQuery();
        }

        return boolBuilder.build()._toQuery();
    }

    /**
     * Builds match_phrase queries for exact phrase matching across all searchable fields.
     * Uses the .phrase subfields which have standard tokenization (serbian_phrase_analyzer)
     * instead of ngrams, making them suitable for true phrase matching.
     * 
     * For single-word phrases: uses both match_phrase (exact) and match (partial) for flexibility.
     * For multi-word phrases: uses ONLY match_phrase for strict exact matching.
     */
    private List<Query> buildPhraseQueries(String phrase) {
        List<Query> queries = new ArrayList<>();
        
        log.info("Building phrase queries for: '{}'", phrase);
        
        // Check if this is a single word or multi-word phrase
        boolean isSingleWord = !phrase.contains(" ");
        log.info("  Phrase type: {}", isSingleWord ? "single word" : "multi-word");

        // ALWAYS add exact phrase matches (high boost)
        queries.add(MatchPhraseQuery.of(m -> m
                .field("ime.phrase")
                .query(phrase)
                .boost(5.0f)  // Very high boost for exact phrase in name
        )._toQuery());
        log.info("  Added match_phrase: ime.phrase='{}' (boost=5.0)", phrase);

        queries.add(MatchPhraseQuery.of(m -> m
                .field("opis.phrase")
                .query(phrase)
                .boost(3.0f)  // High boost for exact phrase in description
        )._toQuery());
        log.info("  Added match_phrase: opis.phrase='{}' (boost=3.0)", phrase);

        queries.add(MatchPhraseQuery.of(m -> m
                .field("pdfContent.phrase")
                .query(phrase)
                .boost(2.0f)  // Medium boost for exact phrase in PDF
        )._toQuery());
        log.info("  Added match_phrase: pdfContent.phrase='{}' (boost=2.0)", phrase);

        // ONLY for single-word phrases: add match queries for partial matching
        // This allows "puno" to match documents containing "puno anime"
        // But prevents "puno anime" from matching documents with just "puno" or just "anime"
        if (isSingleWord) {
            queries.add(MatchQuery.of(m -> m
                    .field("ime.phrase")
                    .query(phrase)
                    .boost(1.5f)  // Lower boost for word match in name
            )._toQuery());
            log.info("  Added match: ime.phrase='{}' (boost=1.5) - single word flexibility", phrase);

            queries.add(MatchQuery.of(m -> m
                    .field("opis.phrase")
                    .query(phrase)
                    .boost(1.0f)  // Lower boost for word match in description
            )._toQuery());
            log.info("  Added match: opis.phrase='{}' (boost=1.0) - single word flexibility", phrase);

            queries.add(MatchQuery.of(m -> m
                    .field("pdfContent.phrase")
                    .query(phrase)
                    .boost(0.5f)  // Lowest boost for word match in PDF
            )._toQuery());
            log.info("  Added match: pdfContent.phrase='{}' (boost=0.5) - single word flexibility", phrase);
        } else {
            log.info("  Skipped match queries - multi-word phrase requires exact matching only");
        }

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

    /**
     * Builds a range query for numeric fields like reviewCount
     * Supports queries like reviews:<10, reviews:>5, reviews:5-10
     */
    private Query buildRangeQuery(SearchQueryParser.RangeQuery rangeQuery) {
        log.info("Building range query for field: {}, min: {}, max: {}", 
                rangeQuery.getFieldName(), rangeQuery.getMinValue(), rangeQuery.getMaxValue());

        // Map common field aliases to actual field names
        String fieldName = rangeQuery.getFieldName();
        if ("reviews".equalsIgnoreCase(fieldName) || "review".equalsIgnoreCase(fieldName)) {
            fieldName = "reviewCount";
        }

        final String finalFieldName = fieldName;

        return RangeQuery.of(r -> {
            r.field(finalFieldName);
            if (rangeQuery.hasMin()) {
                r.gte(JsonData.of(rangeQuery.getMinValue()));
            }
            if (rangeQuery.hasMax()) {
                r.lte(JsonData.of(rangeQuery.getMaxValue()));
            }
            return r;
        })._toQuery();
    }
}
