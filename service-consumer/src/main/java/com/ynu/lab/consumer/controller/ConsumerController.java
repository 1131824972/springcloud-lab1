package com.ynu.lab.consumer.controller;

import com.ynu.lab.consumer.client.ProviderClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class ConsumerController {

    private final ProviderClient providerClient;

    @Value("${server.port}")
    private String port;

    @Value("${lab.message:consumer default message}")
    private String message;

    public ConsumerController(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    @GetMapping("/test")
    public String testCall() {
        String result = providerClient.getHelloFromProvider();
        return "Consumer received provider response ---> " + result;
    }

    @GetMapping("/config")
    public String config() {
        return "consumer config message: " + message + ", port: " + port;
    }

    @GetMapping("/test-cb-a")
    @CircuitBreaker(name = "circuitBreakerA", fallbackMethod = "cbAFallback")
    public String testCircuitBreakerA() {
        String result = providerClient.getErrorFromProvider();
        return "CircuitBreakerA response ---> " + result;
    }

    public String cbAFallback(Exception e) {
        System.out.println("[CircuitBreakerA] fallback triggered: " + e.getMessage());
        return "[Fallback] CircuitBreakerA opened because provider failures exceeded the threshold.";
    }

    @GetMapping("/test-cb-b")
    @CircuitBreaker(name = "circuitBreakerB", fallbackMethod = "cbBFallback")
    public String testCircuitBreakerB() {
        String result = providerClient.getSlowFromProvider();
        return "CircuitBreakerB response ---> " + result;
    }

    public String cbBFallback(Exception e) {
        System.out.println("[CircuitBreakerB] fallback triggered: " + e.getMessage());
        return "[Fallback] CircuitBreakerB opened because slow calls or failures exceeded the threshold.";
    }

    @GetMapping("/test-bulkhead")
    @Bulkhead(name = "bulkheadA", fallbackMethod = "bulkheadFallback")
    public String testBulkhead() throws InterruptedException {
        Thread.sleep(1000);
        String result = providerClient.getHelloFromProvider();
        return "Bulkhead test response ---> " + result;
    }

    public String bulkheadFallback(Exception e) {
        System.out.println("[Bulkhead] fallback triggered: " + e.getMessage());
        return "[Fallback] Bulkhead rejected the request because concurrent calls are too high.";
    }

    @GetMapping("/test-ratelimiter")
    @RateLimiter(name = "rateLimiterA", fallbackMethod = "rateLimiterFallback")
    public String testRateLimiter() {
        String result = providerClient.getHelloFromProvider();
        return "RateLimiter test response ---> " + result;
    }

    public String rateLimiterFallback(Exception e) {
        System.out.println("[RateLimiter] fallback triggered: " + e.getMessage());
        return "[Fallback] RateLimiter rejected the request because requests are too frequent.";
    }
}
