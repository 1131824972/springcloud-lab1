package com.ynu.lab.lab2.consumer.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// 自定义 Nacos 权重负载均衡器
public class NacosWeightLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final String serviceId;

    public NacosWeightLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        this.serviceId = serviceId;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable();
        if (supplier == null) {
            return Mono.just(new EmptyResponse());
        }
        // 获取所有存活的实例，并移交给我们的核心算法进行挑选
        return supplier.get(request).next().map(this::getInstanceResponse);
    }

    // 核心权重计算算法
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return new EmptyResponse();
        }

        // 1. 计算所有实例的总权重
        double totalWeight = 0.0;
        for (ServiceInstance instance : instances) {
            // Nacos 的权重存储在元数据 metadata 中，默认值为 1.0
            String weightStr = instance.getMetadata().getOrDefault("nacos.weight", "1.0");
            totalWeight += Double.parseDouble(weightStr);
        }

        // 2. 生成一个 0 到 总权重 之间的随机浮点数
        double randomWeight = ThreadLocalRandom.current().nextDouble(totalWeight);

        // 3. 轮询累加，判断随机数落在哪个实例的权重区间内
        double currentWeight = 0.0;
        for (ServiceInstance instance : instances) {
            String weightStr = instance.getMetadata().getOrDefault("nacos.weight", "1.0");
            currentWeight += Double.parseDouble(weightStr);
            // 如果落入区间，就选择该实例
            if (randomWeight <= currentWeight) {
                System.out.println("权重负载均衡触发，选中端口：" + instance.getPort() + "，该节点权重：" + weightStr);
                return new DefaultResponse(instance);
            }
        }

        // 兜底策略：如果出现异常，随机返回一个
        return new DefaultResponse(instances.get(ThreadLocalRandom.current().nextInt(instances.size())));
    }
}