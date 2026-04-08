package com.ynu.lab.consumer.controller;

import com.ynu.lab.consumer.client.ProviderClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    private final ProviderClient providerClient;

    // 构造器注入
    public ConsumerController(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    @GetMapping("/test")
    public String testCall() {
        //调用远程接口
        String result = providerClient.getHelloFromProvider();
        return "消费者接收到回传数据 ---> " + result;
    }
}