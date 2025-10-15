package com.example.sss.config;

import com.example.sss.servisi.CentarServis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Automatically indexes all centers in Elasticsearch when the application starts.
 * This ensures search functionality works immediately after deployment.
 */
@Component
@Slf4j
public class StartupIndexer implements ApplicationRunner {

    @Autowired
    private CentarServis centarServis;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("========================================");
            log.info("Starting automatic Elasticsearch indexing...");
            log.info("========================================");
            
            centarServis.indexAllCentri();
            
            log.info("========================================");
            log.info("Elasticsearch indexing completed successfully!");
            log.info("========================================");
        } catch (Exception e) {
            log.error("========================================");
            log.error("Failed to index centers on startup", e);
            log.error("Search functionality may not work properly");
            log.error("Please run /api/centri/reindex endpoint manually");
            log.error("========================================");
        }
    }
}
