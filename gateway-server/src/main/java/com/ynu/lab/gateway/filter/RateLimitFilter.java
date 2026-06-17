package com.ynu.lab.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {
    private final ConcurrentHashMap<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Value("${gateway.rate-limit.window-seconds:2}")
    private long windowSeconds;

    @Value("${gateway.rate-limit.max-requests:5}")
    private int maxRequests;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod()) || path.startsWith("/actuator") || path.equals("/fallback")) {
            return chain.filter(exchange);
        }

        String key = clientKey(exchange);
        long window = Instant.now().getEpochSecond() / windowSeconds;
        WindowCounter counter = counters.compute(key, (ignored, current) -> {
            if (current == null || current.window != window) {
                return new WindowCounter(window);
            }
            current.count.incrementAndGet();
            return current;
        });

        if (counter.count.get() > maxRequests) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
            byte[] body = "429 Too Many Requests: gateway rate limit exceeded".getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -90;
    }

    private String clientKey(ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return "unknown";
        }
        return remoteAddress.getAddress().getHostAddress();
    }

    private static class WindowCounter {
        private final long window;
        private final AtomicInteger count = new AtomicInteger(1);

        private WindowCounter(long window) {
            this.window = window;
        }
    }
}
