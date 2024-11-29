package com.rin.apigateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rin.apigateway.dto.ApiResponse;
import com.rin.apigateway.service.IdentityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Order {
    // Service for token introspection (checking token validity)
    IdentityService identityService;
    ObjectMapper objectMapper;

    // List of public endpoints that don't require authentication

    private final String[] publicEndpoint = {
            "/identity/auth/.*",  // Regex pattern matching
            "/identity/users/create",  // Public user creation endpoint
            "/identity/auth/outbound/google-login",
    };

    private final String[] publicGetEndpoints = {
            "/kanban/products",
            "/kanban/categories",
            "/kanban/sub-products",
            "/kanban/promotions",
            "/kanban/categories/root",
            "/kanban/products/bestseller",
            "/kanban/products/.*",
            "/kanban/sub-products/product-detail/.*",
            "kanban/categories/get-tree",
            "/locations/.*"
    };

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    private final ApiResponse apiUnauthenticatedResponse = ApiResponse.builder()
            .code(500)
            .message("Unauthenticated")
            .build();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Authentication Filter...");

        String httpMethod = exchange.getRequest().getMethod().name();

        // Skip authentication if the request is for a public endpoint
        if (isPublicEndpoint(exchange.getRequest(), httpMethod)){
            log.info("Public Endpoint");
            return chain.filter(exchange);// Continue the request without authentication
        }
        // Get the Authorization header from the request
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        log.info("Authorization header: {}", authHeader);

        // If no Authorization header, return 401 Unauthorized
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse(), apiUnauthenticatedResponse);

        // Remove the "Bearer" prefix and extract the token
        String token = authHeader.getFirst().replace("Bearer", "");
        log.info("Authentication Token: {}", token);

        // Validate the token using the IdentityService
        return identityService.introspect(token).flatMap(introspectResponseApiResponse -> {
            // If token is valid, continue the request
            if (introspectResponseApiResponse.getCode() != 1000) {
                return unauthenticated(exchange.getResponse(), apiUnauthenticatedResponse);
            }

            if (introspectResponseApiResponse.getResult().isValid()) {
                return chain.filter(exchange);
            } else {
                // If token is invalid, return 401 Unauthorized
                return unauthenticated(exchange.getResponse(), apiUnauthenticatedResponse);
            }
        }).onErrorResume(
                // Handle errors during token introspection
                throwable -> unauthenticated(exchange.getResponse(), apiUnauthenticatedResponse
                        ));
    }

    // Helper method to return a 401 Unauthorized response
    private Mono<Void> unauthenticated(ServerHttpResponse response, ApiResponse<?> apiResponse) {
        String body = null;
        try {
            // Convert ApiResponse to JSON format
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // Handle JSON processing exception
        }

        // Set the response status to 401 and content type to JSON
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // Send the response body as a byte buffer
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }


    // Check if the request URI matches any public endpoint pattern
    private boolean isPublicEndpoint(ServerHttpRequest request, String httpMethod) {
        log.info("Path: {}", request.getURI().getPath());
        // Stream through publicEndpoint array to match request path
        if ("GET".equalsIgnoreCase(httpMethod)) {
            return Arrays.stream(publicGetEndpoints).anyMatch(s -> request.getURI().getPath().matches(s));
        }
        return Arrays.stream(publicEndpoint).anyMatch(s -> request.getURI().getPath().matches(s));
    }

    // Set the order of this filter, where -1 means it runs early in the filter chain
    @Override
    public int value() {
        return -1;
    }
}
