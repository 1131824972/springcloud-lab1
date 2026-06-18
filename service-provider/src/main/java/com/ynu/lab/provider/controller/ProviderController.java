package com.ynu.lab.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class ProviderController {

    @Value("${server.port}")
    private String port;

    @Value("${lab.message:provider default message}")
    private String message;

    @GetMapping("/hello")
    public String sayHello() {
        return "Service provider called, port: " + port;
    }

    @GetMapping("/config")
    public String config() {
        return "provider config message: " + message + ", port: " + port;
    }

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(3000);
        return "Slow provider response after 3 seconds, port: " + port;
    }

    @GetMapping("/error")
    public String error() {
        throw new RuntimeException("Provider port " + port + " simulated error");
    }
}
