package com.example.sss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Applies custom field mappings to Elasticsearch index after it's created.
 * This adds .phrase subfields to ime, opis, and pdfContent for proper phrase matching.
 */
@Component
@Slf4j
public class ElasticsearchIndexConfiguration {

    private static final String MAPPINGS_JSON = """
            {
              "properties": {
                "ime": {
                  "type": "text",
                  "analyzer": "serbian_ngram_analyzer",
                  "search_analyzer": "serbian_search_analyzer",
                  "fields": {
                    "phrase": {
                      "type": "text",
                      "analyzer": "serbian_phrase_analyzer"
                    }
                  }
                },
                "opis": {
                  "type": "text",
                  "analyzer": "serbian_ngram_analyzer",
                  "search_analyzer": "serbian_search_analyzer",
                  "fields": {
                    "phrase": {
                      "type": "text",
                      "analyzer": "serbian_phrase_analyzer"
                    }
                  }
                },
                "pdfContent": {
                  "type": "text",
                  "analyzer": "serbian_ngram_analyzer",
                  "search_analyzer": "serbian_search_analyzer",
                  "fields": {
                    "phrase": {
                      "type": "text",
                      "analyzer": "serbian_phrase_analyzer"
                    }
                  }
                }
              }
            }
            """;

    @EventListener(ApplicationReadyEvent.class)
    @Order(100)  // Run after StartupIndexer
    public void applyMappings() {
        try {
            // Wait for index to be created and populated
            Thread.sleep(10000);
            
            log.info("========================================");
            log.info("Applying multi-field mappings to 'centri' index...");
            log.info("========================================");
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9200/centri/_mapping"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(MAPPINGS_JSON))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.info("✅ Multi-field mappings applied successfully!");
                log.info("✅ Fields ime.phrase, opis.phrase, pdfContent.phrase are now available for exact phrase matching");
                log.info("========================================");
            } else {
                log.error("❌ Failed to apply mappings. Status: " + response.statusCode());
                log.error("Response: " + response.body());
            }
        } catch (Exception e) {
            log.error("❌ Error applying custom mappings: " + e.getMessage(), e);
        }
    }
}
