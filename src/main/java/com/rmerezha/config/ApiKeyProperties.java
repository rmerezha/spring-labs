package com.rmerezha.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security.api-key")
public class ApiKeyProperties {
    private String secret;

    private String headerName = "X-API-KEY";

    private String username = "API-KEY-USER";

    private String role = "ROLE_API";
}
