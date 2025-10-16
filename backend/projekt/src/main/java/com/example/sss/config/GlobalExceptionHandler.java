package com.example.sss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.error("File upload size exceeded: {}", exc.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "File too large");
        error.put("message", "Maximum upload size is 50MB. Please choose smaller files.");
        error.put("details", exc.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(error);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, String>> handleMultipartException(MultipartException exc) {
        log.error("Multipart upload error: {}", exc.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "File upload error");
        error.put("message", "There was an error uploading the files. Please check file format and size.");
        error.put("details", exc.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
