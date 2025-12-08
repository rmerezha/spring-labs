package com.rmerezha.security;

import com.rmerezha.config.ApiKeyProperties;
import com.rmerezha.config.JwtProperties;
import com.rmerezha.service.ProductService;
import com.rmerezha.web.ProductController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("security")
@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class})
@EnableConfigurationProperties({ApiKeyProperties.class, JwtProperties.class})
@TestPropertySource(properties = {
        "app.security.api-key.header-name=X-API-KEY",
        "app.security.api-key.secret=cosmo-secret-key-123",
        "app.security.api-key.role=ROLE_ADMIN",
        "app.security.jwt.secret=my-very-secret-key-for-tests-only-must-be-long-enough",
        "app.security.jwt.algorithm=HS256"
})
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private com.rmerezha.mapper.ProductMapper productMapper;

    @Test
    @DisplayName("authenticate: should return 200 OK when JWT is valid")
    void testAuthenticate_WhenJwtIsValid_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .with(jwt().authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("authenticate: should return 200 OK when API Key is valid")
    void testAuthenticate_WhenApiKeyIsValid_ReturnsOk() throws Exception {
        String apiKeyHeader = "X-API-KEY";
        String apiKeyValue = "cosmo-secret-key-123";

        mockMvc.perform(get("/api/v1/products")
                        .header(apiKeyHeader, apiKeyValue))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("authenticate: should return 401 Unauthorized when API Key is invalid")
    void testAuthenticate_WhenApiKeyIsInvalid_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("X-API-KEY", "WRONG_KEY"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("authenticate: should return 401 Unauthorized when user is anonymous")
    void testAuthenticate_WhenAnonymous_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }
}