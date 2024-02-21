package org.example.orderservice.config;

import lombok.RequiredArgsConstructor;
import org.example.ApiClient;
import org.example.rest.ProductApi;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class ProductServiceConfiguration {

    private final TokenPropagationHandler tokenPropagationHandler;

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .interceptors(tokenPropagationHandler);
    }

    @Bean
    public ApiClient customApiClient() {
        RestTemplate restTemplate = restTemplateBuilder().build();
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setDebugging(true);
        apiClient.setBasePath("http://localhost:8080/");
        return apiClient;
    }

    @Bean
    public ApiClient apiClientWithAuth() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBearerToken(() -> {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return jwtAuthenticationToken.getToken().getTokenValue();
        });
        apiClient.setMaxAttemptsForRetry(10);
        apiClient.setWaitTimeMillis(1000);
        apiClient.setDebugging(true);
        return apiClient;
    }

    @Bean
    public ProductApi productApi() {
        return new ProductApi(apiClientWithAuth());
    }
}
