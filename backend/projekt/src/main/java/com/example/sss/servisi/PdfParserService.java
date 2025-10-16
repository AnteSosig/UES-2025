package com.example.sss.servisi;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class PdfParserService {

    /**
     * Extract text content from PDF file
     */
    public String extractTextFromPdf(MultipartFile pdfFile) {
        try (InputStream inputStream = pdfFile.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            if (document.isEncrypted()) {
                log.error("PDF file is encrypted");
                throw new RuntimeException("Cannot process encrypted PDF files");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            log.info("Successfully extracted text from PDF. Length: {} characters", text.length());
            return text;

        } catch (IOException e) {
            log.error("Error extracting text from PDF", e);
            throw new RuntimeException("Error extracting text from PDF", e);
        }
    }

    /**
     * Extract text content from PDF InputStream
     */
    public String extractTextFromPdf(InputStream pdfInputStream) {
        try (PDDocument document = PDDocument.load(pdfInputStream)) {

            if (document.isEncrypted()) {
                log.error("PDF file is encrypted");
                throw new RuntimeException("Cannot process encrypted PDF files");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            log.info("Successfully extracted text from PDF. Length: {} characters", text.length());
            return text;

        } catch (IOException e) {
            log.error("Error extracting text from PDF", e);
            throw new RuntimeException("Error extracting text from PDF", e);
        }
    }

    /**
     * Validate if file is a valid PDF
     */
    public boolean isValidPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        return (contentType != null && contentType.equals("application/pdf"))
                || (filename != null && filename.toLowerCase().endsWith(".pdf"));
    }
}
