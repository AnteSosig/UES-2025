package com.example.sss.servisi.implementacije;

import com.example.sss.model.Centar;
import com.example.sss.model.elasticsearch.CentarDocument;
import com.example.sss.repozitorijumi.CentarRepozitorijum;
import com.example.sss.repozitorijumi.CentarElasticsearchRepository;
import com.example.sss.servisi.CentarServis;
import com.example.sss.servisi.ElasticsearchQueryService;
import com.example.sss.servisi.MinioService;
import com.example.sss.servisi.PdfParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CentarServisImpl implements CentarServis {

    @Autowired
    private CentarRepozitorijum centarRepozitorijum;

    @Autowired
    private CentarElasticsearchRepository elasticsearchRepository;

    @Autowired
    private ElasticsearchQueryService elasticsearchQueryService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private PdfParserService pdfParserService;

    @Autowired
    private com.example.sss.repozitorijumi.OcenaRepozitorijum ocenaRepozitorijum;

    @Override
    public List<Centar> getAll() {
        log.debug("Fetching all centers");
        return centarRepozitorijum.findAll();
    }

    @Override
    @Transactional
    public Centar save(Centar centar) {
        Centar savedCentar = centarRepozitorijum.save(centar);
        indexCentar(savedCentar);
        return savedCentar;
    }

    @Override
    @Transactional
    public Centar uploadFiles(Integer centarId, MultipartFile image, MultipartFile pdf) {
        Centar centar = centarRepozitorijum.findById(centarId);
        if (centar == null) {
            throw new RuntimeException("Center not found with id: " + centarId);
        }

        String pdfContent = null;

        try {
            // Upload image if provided
            if (image != null && !image.isEmpty()) {
                String imagePath = minioService.uploadFile(image, "images");
                centar.setImagePath(imagePath);
                log.info("Image uploaded for center {}: {}", centarId, imagePath);
            }

            // Upload PDF and extract content if provided
            if (pdf != null && !pdf.isEmpty()) {
                if (!pdfParserService.isValidPdf(pdf)) {
                    throw new RuntimeException("Invalid PDF file");
                }

                // Extract text from PDF (will be stored in Elasticsearch only)
                pdfContent = pdfParserService.extractTextFromPdf(pdf);

                // Upload PDF to MinIO
                String pdfPath = minioService.uploadFile(pdf, "pdfs");
                centar.setPdfPath(pdfPath);
                
                log.info("PDF uploaded for center {}: {}", centarId, pdfPath);
                log.info("PDF content extracted, length: {} characters", pdfContent.length());
            }

            // Save updated center (without pdfContent - that goes only to Elasticsearch)
            Centar updatedCentar = centarRepozitorijum.save(centar);

            // Update Elasticsearch index with PDF content
            indexCentar(updatedCentar, pdfContent);

            return updatedCentar;

        } catch (Exception e) {
            log.error("Error uploading files for center {}", centarId, e);
            throw new RuntimeException("Error uploading files: " + e.getMessage(), e);
        }
    }

    @Override
    public void indexCentar(Centar centar, String pdfContent) {
        try {
            CentarDocument document = new CentarDocument();
            document.setId(centar.getId());
            document.setIme(centar.getIme());
            document.setOpis(centar.getOphis());
            document.setDatumKreacije(centar.getDatumKreacije());
            document.setAdresa(centar.getAdresa());
            document.setGrad(centar.getGrad());
            document.setRating(centar.getRating());
            document.setActive(centar.isActive());
            document.setImagePath(centar.getImagePath());
            document.setPdfPath(centar.getPdfPath());
            
            // Set PDF content only in Elasticsearch (not stored in MySQL)
            if (pdfContent != null && !pdfContent.isEmpty()) {
                document.setPdfContent(pdfContent);
            }

            // Count reviews for this center
            int reviewCount = ocenaRepozitorijum.oceneObjekta(centar.getId()).size();
            document.setReviewCount(reviewCount);
            log.debug("Center {} has {} reviews", centar.getId(), reviewCount);

            elasticsearchRepository.save(document);
            log.info("Center {} indexed successfully in Elasticsearch with {} reviews", 
                    centar.getId(), reviewCount);
        } catch (Exception e) {
            log.error("Error indexing center {} in Elasticsearch", centar.getId(), e);
            throw new RuntimeException("Error indexing center", e);
        }
    }

    @Override
    public void indexCentar(Centar centar) {
        // When called without pdfContent parameter, check if center has PDF and extract it
        String pdfContent = null;
        
        if (centar.getPdfPath() != null && !centar.getPdfPath().isEmpty()) {
            try {
                log.debug("Re-extracting PDF content for center {} during re-index", centar.getId());
                InputStream pdfStream = minioService.downloadFile(centar.getPdfPath());
                pdfContent = pdfParserService.extractTextFromPdf(pdfStream);
                log.debug("PDF content re-extracted for center {}, length: {} characters", 
                        centar.getId(), pdfContent.length());
            } catch (Exception e) {
                log.warn("Failed to re-extract PDF content for center {} during re-index: {}", 
                        centar.getId(), e.getMessage());
                // Continue indexing without PDF content
            }
        }
        
        indexCentar(centar, pdfContent);
    }

    @Override
    public void indexAllCentri() {
        try {
            List<Centar> allCentri = centarRepozitorijum.findAll();
            for (Centar centar : allCentri) {
                String pdfContent = null;
                
                // If center has a PDF, re-extract its content from MinIO
                if (centar.getPdfPath() != null && !centar.getPdfPath().isEmpty()) {
                    try {
                        log.debug("Re-extracting PDF content for center {}", centar.getId());
                        InputStream pdfStream = minioService.downloadFile(centar.getPdfPath());
                        pdfContent = pdfParserService.extractTextFromPdf(pdfStream);
                        log.debug("PDF content re-extracted for center {}, length: {} characters", 
                                centar.getId(), pdfContent.length());
                    } catch (Exception e) {
                        log.warn("Failed to re-extract PDF content for center {}: {}", 
                                centar.getId(), e.getMessage());
                        // Continue indexing without PDF content
                    }
                }
                
                indexCentar(centar, pdfContent);
            }
            log.info("All {} centers indexed successfully", allCentri.size());
        } catch (Exception e) {
            log.error("Error indexing all centers", e);
            throw new RuntimeException("Error indexing all centers", e);
        }
    }
    
    /**
     * Re-indexes all centers and returns the count.
     * Used after applying new Elasticsearch mappings to populate new fields.
     */
    public int reindexAllCenters() {
        try {
            List<Centar> allCentri = centarRepozitorijum.findAll();
            for (Centar centar : allCentri) {
                String pdfContent = null;
                
                if (centar.getPdfPath() != null && !centar.getPdfPath().isEmpty()) {
                    try {
                        InputStream pdfStream = minioService.downloadFile(centar.getPdfPath());
                        pdfContent = pdfParserService.extractTextFromPdf(pdfStream);
                    } catch (Exception e) {
                        log.warn("Failed to re-extract PDF for center {}: {}", 
                                centar.getId(), e.getMessage());
                    }
                }
                
                indexCentar(centar, pdfContent);
            }
            log.info("All {} centers re-indexed successfully", allCentri.size());
            return allCentri.size();
        } catch (Exception e) {
            log.error("Error re-indexing all centers", e);
            throw new RuntimeException("Error re-indexing all centers", e);
        }
    }

    @Override
    public List<CentarDocument> searchByNaziv(String naziv) {
        log.debug("Searching centers by naziv: {}", naziv);
        return elasticsearchRepository.findByImeContaining(naziv);
    }

    @Override
    public List<CentarDocument> searchByOpis(String opis) {
        log.debug("Searching centers by opis: {}", opis);
        return elasticsearchRepository.findByOpisContaining(opis);
    }

    @Override
    public List<CentarDocument> searchByPdfContent(String content) {
        log.debug("Searching centers by PDF content: {}", content);
        return elasticsearchRepository.findByPdfContentContaining(content);
    }

    @Override
    public List<CentarDocument> searchAll(String query) {
        log.debug("Searching centers by all fields: {}", query);
        try {
            // Use new ElasticsearchQueryService with phrase matching support
            return elasticsearchQueryService.searchWithPhraseSupport(query);
        } catch (Exception e) {
            log.error("Error searching with phrase support, falling back to custom query", e);
            try {
                // Fallback to custom query with Serbian analyzer
                return elasticsearchRepository.searchByQuery(query);
            } catch (Exception e2) {
                log.error("Error with custom query, falling back to basic search", e2);
                // Final fallback to basic search
                return elasticsearchRepository.findByImeContainingOrOpisContainingOrPdfContentContaining(
                        query, query, query);
            }
        }
    }

    @Override
    public InputStream downloadPdf(Integer centarId) {
        Centar centar = centarRepozitorijum.findById(centarId);
        if (centar == null) {
            throw new RuntimeException("Center not found with id: " + centarId);
        }
        if (centar.getPdfPath() == null || centar.getPdfPath().isEmpty()) {
            throw new RuntimeException("No PDF document found for center: " + centarId);
        }

        try {
            return minioService.downloadFile(centar.getPdfPath());
        } catch (Exception e) {
            log.error("Error downloading PDF for center {}", centarId, e);
            throw new RuntimeException("Error downloading PDF", e);
        }
    }

    @Override
    public InputStream downloadImage(Integer centarId) {
        Centar centar = centarRepozitorijum.findById(centarId);
        if (centar == null) {
            throw new RuntimeException("Center not found with id: " + centarId);
        }
        if (centar.getImagePath() == null || centar.getImagePath().isEmpty()) {
            throw new RuntimeException("No image found for center: " + centarId);
        }

        try {
            return minioService.downloadFile(centar.getImagePath());
        } catch (Exception e) {
            log.error("Error downloading image for center {}", centarId, e);
            throw new RuntimeException("Error downloading image", e);
        }
    }
}
