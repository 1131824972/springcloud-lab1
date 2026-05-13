package com.ynu.lab.lab2.consumer;

import com.ynu.lab.lab2.consumer.config.NacosWeightLoadBalancerConfig;
import com.ynu.lab.lab2.consumer.config.RandomLoadBalancerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
@LoadBalancerClient(name = "lab2-provider", configuration = NacosWeightLoadBalancerConfig.class)
public class Lab2ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lab2ConsumerApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}