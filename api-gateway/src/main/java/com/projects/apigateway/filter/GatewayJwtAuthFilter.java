package com.projects.apigateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projects.apigateway.config.SecurityProperties;
import com.projects.apigateway.error.APIError;
import com.projects.apigateway.service.JwtGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class GatewayJwtAuthFilter implements GlobalFilter, Ordered {

    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtGatewayService jwtGatewayService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        var request = exchange.getRequest();
        var path = request.getURI().getPath();

        var isPublic = securityProperties.getPublicRoutes().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (isPublic){
            log.info("Public route, continue: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header for path: {}", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        var token = authHeader.substring(7);

        try {
            jwtGatewayService.validateToken(token);
            log.info("JWT token valid for path {}", path);
        }catch (Exception e){
            log.error("JWT Validation failed for Gateway: {}", e.getMessage());
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        return chain.filter(exchange);
    }

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        var apiError = new APIError(status, message);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiError);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error serializing gateway error response", e);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
