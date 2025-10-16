package com.example.sss.util;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse search queries and extract quoted phrases for exact matching.
 * Supports queries like: word1 "exact phrase" word2 "another phrase"
 */
public class SearchQueryParser {

    /**
     * Parses a search query and separates it into exact phrase matches and regular terms
     */
    public static ParsedQuery parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ParsedQuery(new ArrayList<>(), new ArrayList<>());
        }

        List<String> exactPhrases = new ArrayList<>();
        List<String> regularTerms = new ArrayList<>();

        // Pattern to match quoted strings (supports both "" and "")
        Pattern pattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(query);

        // Track positions of quoted text
        List<int[]> quotedRanges = new ArrayList<>();
        
        while (matcher.find()) {
            String phrase = matcher.group(1).trim();
            if (!phrase.isEmpty()) {
                exactPhrases.add(phrase);
                quotedRanges.add(new int[]{matcher.start(), matcher.end()});
            }
        }

        // Extract non-quoted terms
        StringBuilder remainingText = new StringBuilder(query);
        
        // Remove quoted sections from back to front to maintain indices
        for (int i = quotedRanges.size() - 1; i >= 0; i--) {
            int[] range = quotedRanges.get(i);
            remainingText.delete(range[0], range[1]);
        }

        // Split remaining text by whitespace and add as regular terms
        String[] terms = remainingText.toString().trim().split("\\s+");
        for (String term : terms) {
            term = term.trim();
            if (!term.isEmpty()) {
                regularTerms.add(term);
            }
        }

        return new ParsedQuery(exactPhrases, regularTerms);
    }

    /**
     * Data class to hold parsed query results
     */
    @Data
    public static class ParsedQuery {
        private final List<String> exactPhrases;
        private final List<String> regularTerms;

        public ParsedQuery(List<String> exactPhrases, List<String> regularTerms) {
            this.exactPhrases = exactPhrases;
            this.regularTerms = regularTerms;
        }

        public boolean hasExactPhrases() {
            return exactPhrases != null && !exactPhrases.isEmpty();
        }

        public boolean hasRegularTerms() {
            return regularTerms != null && !regularTerms.isEmpty();
        }

        public boolean isEmpty() {
            return !hasExactPhrases() && !hasRegularTerms();
        }
    }
}
