package com.example.sss.config;

import com.example.sss.servisi.implementacije.CentarServisImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Applies custom multi-field mappings to Elasticsearch index after startup.
 * This adds .phrase subfields to ime, opis, and pdfContent for proper phrase matching.
 * Then triggers a re-index to populate the new subfields.
 */
@Component
@Slf4j
public class ElasticsearchIndexConfiguration {
    
    @Autowired
    private CentarServisImpl centarServis;

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
    @Order(200)  // Run AFTER StartupIndexer (which is Order 100)
    public void applyMultiFieldMappings() {
        try {
            // Wait for initial indexing to complete (StartupIndexer)
            Thread.sleep(5000);
            
            log.info("========================================");
            log.info("Applying multi-field mappings to 'centri' index...");
            log.info("========================================");
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://elasticsearch:9200/centri/_mapping"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(MAPPINGS_JSON))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.info("✅ Multi-field mappings applied successfully!");
                log.info("✅ Fields ime.phrase, opis.phrase, pdfContent.phrase are now available");
                
                // CRITICAL: Trigger re-index to populate the new .phrase subfields
                log.info("========================================");
                log.info("🔄 Re-indexing all centers to populate .phrase subfields...");
                log.info("========================================");
                
                Thread.sleep(2000); // Give Elasticsearch a moment to apply the mapping
                
                int reindexCount = centarServis.reindexAllCenters();
                
                log.info("========================================");
                log.info("✅ Re-indexing complete! {} centers now have phrase search support", reindexCount);
                log.info("✅ Quoted searches are now fully operational!");
                log.info("========================================");
            } else {
                log.error("❌ Failed to apply mappings. Status: " + response.statusCode());
                log.error("Response: " + response.body());
                log.error("⚠️  Phrase searches will not work until mappings are manually applied");
            }
        } catch (Exception e) {
            log.error("❌ Failed to apply multi-field mappings: " + e.getMessage(), e);
            log.error("⚠️  Phrase searches will not work until mappings are manually applied");
        }
    }
}
