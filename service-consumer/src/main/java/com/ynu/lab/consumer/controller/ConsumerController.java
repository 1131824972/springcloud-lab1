package com.ynu.lab.consumer.controller;

import com.ynu.lab.consumer.client.ProviderClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    private final ProviderClient providerClient;

    public ConsumerController(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    // ==================== 原始测试端点 ====================

    @GetMapping("/test")
    public String testCall() {
        String result = providerClient.getHelloFromProvider();
        return "消费者接收到回传数据 ---> " + result;
    }

    // ==================== 断路器A：失败率熔断 ====================

    @GetMapping("/test-cb-a")
    @CircuitBreaker(name = "circuitBreakerA", fallbackMethod = "cbAFallback")
    public String testCircuitBreakerA() {
        String result = providerClient.getErrorFromProvider();
        return "断路器A(失败率) ---> " + result;
    }

    public String cbAFallback(Exception e) {
        System.out.println("[断路器A] 熔断触发(失败率过高): " + e.getMessage());
        return "【服务降级】断路器A已打开(失败率超过30%)，请稍后再试";
    }

    // ==================== 断路器B：慢调用比例熔断 ====================

    @GetMapping("/test-cb-b")
    @CircuitBreaker(name = "circuitBreakerB", fallbackMethod = "cbBFallback")
    public String testCircuitBreakerB() {
        String result = providerClient.getSlowFromProvider();
        return "断路器B(慢调用+失败率) ---> " + result;
    }

    public String cbBFallback(Exception e) {
        System.out.println("[断路器B] 熔断触发(慢调用比例过高): " + e.getMessage());
        return "【服务降级】断路器B已打开(失败率超50%或慢调用比例超30%)，请稍后再试";
    }

    // ==================== 隔离控制 Bulkhead ====================

    @GetMapping("/test-bulkhead")
    @Bulkhead(name = "bulkheadA", fallbackMethod = "bulkheadFallback")
    public String testBulkhead() throws InterruptedException {
        Thread.sleep(1000); // 模拟耗时操作
        String result = providerClient.getHelloFromProvider();
        return "隔离控制测试 ---> " + result;
    }

    public String bulkheadFallback(Exception e) {
        System.out.println("[Bulkhead] 隔离降级触发: " + e.getMessage());
        return "【服务降级】系统繁忙(并发请求过多)，请稍后再试";
    }

    // ==================== 限流控制 RateLimiter ====================

    @GetMapping("/test-ratelimiter")
    @RateLimiter(name = "rateLimiterA", fallbackMethod = "rateLimiterFallback")
    public String testRateLimiter() {
        String result = providerClient.getHelloFromProvider();
        return "限流控制测试 ---> " + result;
    }

    public String rateLimiterFallback(Exception e) {
        System.out.println("[RateLimiter] 限流降级触发: " + e.getMessage());
        return "【服务降级】请求频率过高(限流)，请稍后再试";
    }
}
