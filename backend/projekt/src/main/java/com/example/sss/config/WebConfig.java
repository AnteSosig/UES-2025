package com.example.sss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Specify allowed origins explicitly (more secure than wildcard with credentials)
                        .allowedOriginPatterns(
                            "http://localhost:4200",
                            "http://localhost:3000", 
                            "http://127.0.0.1:4200",
                            "http://127.0.0.1:3000"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                        .allowedHeaders("*")
                        .exposedHeaders("Content-Type", "Content-Disposition", "Authorization", "X-Total-Count")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
