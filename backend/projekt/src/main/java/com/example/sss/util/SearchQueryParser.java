package com.example.sss.util;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse search queries and extract quoted phrases for exact matching.
 * Supports queries like: word1 "exact phrase" word2 "another phrase" reviews:<10 reviews:5-10
 */
public class SearchQueryParser {

    /**
     * Parses a search query and separates it into exact phrase matches, regular terms, and range queries
     */
    public static ParsedQuery parse(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ParsedQuery(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        List<String> exactPhrases = new ArrayList<>();
        List<String> regularTerms = new ArrayList<>();
        List<RangeQuery> rangeQueries = new ArrayList<>();

        // Pattern to match quoted strings (supports both "" and "")
        Pattern quotePattern = Pattern.compile("\"([^\"]+)\"");
        Matcher quoteMatcher = quotePattern.matcher(query);

        // Track positions of quoted text
        List<int[]> quotedRanges = new ArrayList<>();
        
        while (quoteMatcher.find()) {
            String phrase = quoteMatcher.group(1).trim();
            if (!phrase.isEmpty()) {
                exactPhrases.add(phrase);
                quotedRanges.add(new int[]{quoteMatcher.start(), quoteMatcher.end()});
            }
        }

        // Pattern to match range queries like reviews:<10, reviews:>5, reviews:5-10
        Pattern rangePattern = Pattern.compile("(\\w+):(<|>)?(\\d+)(?:-(\\d+))?");
        Matcher rangeMatcher = rangePattern.matcher(query);
        
        // Track positions of range queries
        List<int[]> rangeRanges = new ArrayList<>();
        
        while (rangeMatcher.find()) {
            String fieldName = rangeMatcher.group(1);
            String operator = rangeMatcher.group(2); // < or > or null
            String value1 = rangeMatcher.group(3);
            String value2 = rangeMatcher.group(4); // for range queries like 5-10
            
            RangeQuery rangeQuery = new RangeQuery();
            rangeQuery.setFieldName(fieldName);
            
            if (value2 != null) {
                // Range query like reviews:5-10
                rangeQuery.setMinValue(Integer.parseInt(value1));
                rangeQuery.setMaxValue(Integer.parseInt(value2));
            } else if (operator != null) {
                // Comparison query like reviews:<10 or reviews:>5
                int value = Integer.parseInt(value1);
                if (operator.equals("<")) {
                    rangeQuery.setMaxValue(value - 1); // exclusive
                } else if (operator.equals(">")) {
                    rangeQuery.setMinValue(value + 1); // exclusive
                }
            } else {
                // Exact match like reviews:5
                rangeQuery.setMinValue(Integer.parseInt(value1));
                rangeQuery.setMaxValue(Integer.parseInt(value1));
            }
            
            rangeQueries.add(rangeQuery);
            rangeRanges.add(new int[]{rangeMatcher.start(), rangeMatcher.end()});
        }

        // Extract non-quoted, non-range terms
        StringBuilder remainingText = new StringBuilder(query);
        
        // Remove range queries from back to front to maintain indices
        for (int i = rangeRanges.size() - 1; i >= 0; i--) {
            int[] range = rangeRanges.get(i);
            remainingText.delete(range[0], range[1]);
        }
        
        // Remove quoted sections from back to front to maintain indices
        for (int i = quotedRanges.size() - 1; i >= 0; i--) {
            int[] range = quotedRanges.get(i);
            if (range[0] < remainingText.length()) {
                remainingText.delete(range[0], Math.min(range[1], remainingText.length()));
            }
        }

        // Split remaining text by whitespace and add as regular terms
        String[] terms = remainingText.toString().trim().split("\\s+");
        for (String term : terms) {
            term = term.trim();
            if (!term.isEmpty()) {
                regularTerms.add(term);
            }
        }

        return new ParsedQuery(exactPhrases, regularTerms, rangeQueries);
    }

    /**
     * Data class to hold parsed query results
     */
    @Data
    public static class ParsedQuery {
        private final List<String> exactPhrases;
        private final List<String> regularTerms;
        private final List<RangeQuery> rangeQueries;

        public ParsedQuery(List<String> exactPhrases, List<String> regularTerms, List<RangeQuery> rangeQueries) {
            this.exactPhrases = exactPhrases;
            this.regularTerms = regularTerms;
            this.rangeQueries = rangeQueries;
        }

        public boolean hasExactPhrases() {
            return exactPhrases != null && !exactPhrases.isEmpty();
        }

        public boolean hasRegularTerms() {
            return regularTerms != null && !regularTerms.isEmpty();
        }

        public boolean hasRangeQueries() {
            return rangeQueries != null && !rangeQueries.isEmpty();
        }

        public boolean isEmpty() {
            return !hasExactPhrases() && !hasRegularTerms() && !hasRangeQueries();
        }
    }

    /**
     * Data class to hold range query information
     */
    @Data
    public static class RangeQuery {
        private String fieldName;
        private Integer minValue;
        private Integer maxValue;

        public boolean hasMin() {
            return minValue != null;
        }

        public boolean hasMax() {
            return maxValue != null;
        }
    }
}
