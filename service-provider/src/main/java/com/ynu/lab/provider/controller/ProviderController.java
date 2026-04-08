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
}