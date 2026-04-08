package com.ynu.lab.nacos.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// 绑定我们要调用的提供者名称
@FeignClient(name = "nacos-provider")
public interface NacosProviderClient {

    // 路径必须和提供者那边的 /nacos-hello 一模一样
    @GetMapping("/nacos-hello")
    String fetchHelloFromNacos();
}