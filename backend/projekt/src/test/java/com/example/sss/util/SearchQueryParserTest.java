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
}
