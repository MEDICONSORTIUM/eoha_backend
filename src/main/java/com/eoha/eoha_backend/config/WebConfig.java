package com.eoha.eoha_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * MVC configuration. Implements {@link WebMvcConfigurer} (never {@code @EnableWebMvc},
 * which would switch off Spring Boot's static-resource auto-configuration and the
 * dashboard served from {@code /}).
 *
 * <p>CORS is what lets a browser UI served from another origin call this API. The
 * allowed origins are <em>patterns</em> ({@code allowedOriginPatterns}), so a value
 * like {@code http://localhost:[*]} matches any local dev port (Live Server 5500,
 * Vite 5173, CRA 3000, {@code python -m http.server}, ...). Set
 * {@code eoha.cors.allowed-origins} (or the {@code CORS_ALLOWED_ORIGINS} env var) to
 * the real site origin(s) in production. Note: a page opened as a {@code file://} URL
 * sends {@code Origin: null} and will not match — serve the UI over http.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final List<String> allowedOriginPatterns;

    public WebConfig(@Value("${eoha.cors.allowed-origins:http://localhost:[*]}") List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    /** Exposed for the {@code /api/info} dashboard payload. */
    public List<String> getAllowedOrigins() {
        return allowedOriginPatterns;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOriginPatterns.toArray(String[]::new))
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
