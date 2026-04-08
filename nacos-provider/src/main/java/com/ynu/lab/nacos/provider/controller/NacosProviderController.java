package com.ynu.lab.nacos.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NacosProviderController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/nacos-hello")
    public String helloNacos() {
        return "来自 Nacos 集群的问候！当前提供者端口：" + port;
    }
}