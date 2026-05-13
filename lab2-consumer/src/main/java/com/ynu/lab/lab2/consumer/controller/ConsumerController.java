package com.ynu.lab.lab2.consumer.controller;

import com.ynu.lab.lab2.consumer.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    private final RestTemplate restTemplate;
    private static final String PROVIDER_URL = "http://lab2-provider/user";

    public ConsumerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. 测试 GET 请求
    @GetMapping("/test/get")
    public String testGet() {
        User user = restTemplate.getForObject(PROVIDER_URL + "/101", User.class);
        return "RestTemplate GET 调用成功: " + user;
    }

    // 2. 测试 POST 请求
    @GetMapping("/test/post")
    public String testPost() {
        User newUser = new User(102L, "李四", 22);
        String result = restTemplate.postForObject(PROVIDER_URL + "/add", newUser, String.class);
        return "RestTemplate POST 调用成功: " + result;
    }

    // 3. 测试 PUT 请求
    @GetMapping("/test/put")
    public String testPut() {
        User updateUser = new User(101L, "云南大学学生", 23);
        restTemplate.put(PROVIDER_URL + "/update", updateUser);
        return "RestTemplate PUT 调用成功，已向终端发送修改指令";
    }

    // 4. 测试 DELETE 请求
    @GetMapping("/test/delete")
    public String testDelete() {
        restTemplate.delete(PROVIDER_URL + "/delete/101");
        return "RestTemplate DELETE 调用成功，已向终端发送删除指令";
    }
}