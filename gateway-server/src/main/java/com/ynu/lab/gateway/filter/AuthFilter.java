package com.ynu.lab.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final String VALID_TOKEN = "123";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (HttpMethod.OPTIONS.equals(request.getMethod()) || path.startsWith("/actuator") || path.equals("/fallback")) {
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("X-Token");
        if (token == null || token.isBlank()) {
            token = request.getQueryParams().getFirst("token");
        }

        if (!VALID_TOKEN.equals(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
            byte[] body = "401 Unauthorized: token is missing or invalid".getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
