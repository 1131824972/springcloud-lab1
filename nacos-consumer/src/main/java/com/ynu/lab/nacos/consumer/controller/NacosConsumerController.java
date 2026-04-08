package com.ynu.lab.nacos.consumer.controller;

import com.ynu.lab.nacos.consumer.client.NacosProviderClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NacosConsumerController {

    private final NacosProviderClient nacosProviderClient;

    // 官方推荐的构造器注入，拒绝老旧的 @Autowired
    public NacosConsumerController(NacosProviderClient nacosProviderClient) {
        this.nacosProviderClient = nacosProviderClient;
    }

    @GetMapping("/nacos-test")
    public String testNacosCall() {
        String result = nacosProviderClient.fetchHelloFromNacos();
        return "Nacos 消费者已成功接收数据 ---> " + result;
    }
}