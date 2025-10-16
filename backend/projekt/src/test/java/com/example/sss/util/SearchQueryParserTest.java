package com.example.sss.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for SearchQueryParser
 */
class SearchQueryParserTest {

    @Test
    void testParseSimpleQuery() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("fitness centar");
        
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(2, result.getRegularTerms().size());
        assertTrue(result.getRegularTerms().contains("fitness"));
        assertTrue(result.getRegularTerms().contains("centar"));
    }

    @Test
    void testParseSinglePhrase() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("\"fitness centar\"");
        
        assertEquals(1, result.getExactPhrases().size());
        assertEquals("fitness centar", result.getExactPhrases().get(0));
        assertEquals(0, result.getRegularTerms().size());
    }

    @Test
    void testParseMixedQuery() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("beograd \"fitness centar\" yoga");
        
        assertEquals(1, result.getExactPhrases().size());
        assertEquals("fitness centar", result.getExactPhrases().get(0));
        assertEquals(2, result.getRegularTerms().size());
        assertTrue(result.getRegularTerms().contains("beograd"));
        assertTrue(result.getRegularTerms().contains("yoga"));
    }

    @Test
    void testParseMultiplePhrases() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("\"fitness centar\" word \"health club\"");
        
        assertEquals(2, result.getExactPhrases().size());
        assertTrue(result.getExactPhrases().contains("fitness centar"));
        assertTrue(result.getExactPhrases().contains("health club"));
        assertEquals(1, result.getRegularTerms().size());
        assertTrue(result.getRegularTerms().contains("word"));
    }

    @Test
    void testParseEmptyQuery() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("");
        
        assertTrue(result.isEmpty());
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(0, result.getRegularTerms().size());
    }

    @Test
    void testParseNullQuery() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse(null);
        
        assertTrue(result.isEmpty());
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(0, result.getRegularTerms().size());
    }

    @Test
    void testParseEmptyPhrase() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("word \"\" another");
        
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(2, result.getRegularTerms().size());
        assertTrue(result.getRegularTerms().contains("word"));
        assertTrue(result.getRegularTerms().contains("another"));
    }

    @Test
    void testParseWithExtraSpaces() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("  word1   \"exact phrase\"   word2  ");
        
        assertEquals(1, result.getExactPhrases().size());
        assertEquals("exact phrase", result.getExactPhrases().get(0));
        assertEquals(2, result.getRegularTerms().size());
        assertTrue(result.getRegularTerms().contains("word1"));
        assertTrue(result.getRegularTerms().contains("word2"));
    }

    @Test
    void testParseRangeQueryLessThan() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("reviews:<10");
        
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(0, result.getRegularTerms().size());
        assertEquals(1, result.getRangeQueries().size());
        
        SearchQueryParser.RangeQuery rangeQuery = result.getRangeQueries().get(0);
        assertEquals("reviews", rangeQuery.getFieldName());
        assertNull(rangeQuery.getMinValue());
        assertEquals(9, rangeQuery.getMaxValue()); // exclusive < becomes inclusive <=
    }

    @Test
    void testParseRangeQueryGreaterThan() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("reviews:>5");
        
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(0, result.getRegularTerms().size());
        assertEquals(1, result.getRangeQueries().size());
        
        SearchQueryParser.RangeQuery rangeQuery = result.getRangeQueries().get(0);
        assertEquals("reviews", rangeQuery.getFieldName());
        assertEquals(6, rangeQuery.getMinValue()); // exclusive > becomes inclusive >=
        assertNull(rangeQuery.getMaxValue());
    }

    @Test
    void testParseRangeQueryBetween() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("reviews:5-10");
        
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(0, result.getRegularTerms().size());
        assertEquals(1, result.getRangeQueries().size());
        
        SearchQueryParser.RangeQuery rangeQuery = result.getRangeQueries().get(0);
        assertEquals("reviews", rangeQuery.getFieldName());
        assertEquals(5, rangeQuery.getMinValue());
        assertEquals(10, rangeQuery.getMaxValue());
    }

    @Test
    void testParseRangeQueryExact() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("reviews:5");
        
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(0, result.getRegularTerms().size());
        assertEquals(1, result.getRangeQueries().size());
        
        SearchQueryParser.RangeQuery rangeQuery = result.getRangeQueries().get(0);
        assertEquals("reviews", rangeQuery.getFieldName());
        assertEquals(5, rangeQuery.getMinValue());
        assertEquals(5, rangeQuery.getMaxValue());
    }

    @Test
    void testParseMixedWithRangeQuery() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("fitness \"health club\" reviews:<10 yoga");
        
        assertEquals(1, result.getExactPhrases().size());
        assertEquals("health club", result.getExactPhrases().get(0));
        
        assertEquals(2, result.getRegularTerms().size());
        assertTrue(result.getRegularTerms().contains("fitness"));
        assertTrue(result.getRegularTerms().contains("yoga"));
        
        assertEquals(1, result.getRangeQueries().size());
        SearchQueryParser.RangeQuery rangeQuery = result.getRangeQueries().get(0);
        assertEquals("reviews", rangeQuery.getFieldName());
        assertNull(rangeQuery.getMinValue());
        assertEquals(9, rangeQuery.getMaxValue());
    }

    @Test
    void testParseMultipleRangeQueries() {
        SearchQueryParser.ParsedQuery result = SearchQueryParser.parse("reviews:>5 rating:4-5");
        
        assertEquals(0, result.getExactPhrases().size());
        assertEquals(0, result.getRegularTerms().size());
        assertEquals(2, result.getRangeQueries().size());
        
        // Find the reviews range query
        SearchQueryParser.RangeQuery reviewsQuery = result.getRangeQueries().stream()
                .filter(q -> q.getFieldName().equals("reviews"))
                .findFirst()
                .orElse(null);
        assertNotNull(reviewsQuery);
        assertEquals(6, reviewsQuery.getMinValue());
        assertNull(reviewsQuery.getMaxValue());
        
        // Find the rating range query
        SearchQueryParser.RangeQuery ratingQuery = result.getRangeQueries().stream()
                .filter(q -> q.getFieldName().equals("rating"))
                .findFirst()
                .orElse(null);
        assertNotNull(ratingQuery);
        assertEquals(4, ratingQuery.getMinValue());
        assertEquals(5, ratingQuery.getMaxValue());
    }
}
