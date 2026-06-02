package com.ynu.lab.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "service-provider")
public interface ProviderClient {
    @GetMapping("/hello")
    String getHelloFromProvider();

    @GetMapping("/slow")
    String getSlowFromProvider();

    @GetMapping("/error")
    String getErrorFromProvider();
}