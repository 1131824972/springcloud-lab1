package com.ynu.lab.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/hello")
    public String sayHello() {
        return "服务提供者已被调用！当前运行端口：" + port;
    }

    @GetMapping("/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(3000);
        return "慢调用响应(3秒)，端口：" + port;
    }

    @GetMapping("/error")
    public String error() {
        throw new RuntimeException("服务提供者端口" + port + "模拟异常");
    }
}