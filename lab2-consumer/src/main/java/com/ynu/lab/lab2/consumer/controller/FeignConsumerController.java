package com.ynu.lab.lab2.consumer.controller;

import com.ynu.lab.lab2.consumer.client.UserFeignClient;
import com.ynu.lab.lab2.consumer.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignConsumerController {

    private final UserFeignClient userFeignClient;

    public FeignConsumerController(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @GetMapping("/feign/get")
    public String testFeignGet() {
        User user = userFeignClient.getUserById(201L);
        return "OpenFeign GET 调用成功: " + user;
    }

    @GetMapping("/feign/post")
    public String testFeignPost() {
        User newUser = new User(202L, "王五", 24);
        String result = userFeignClient.addUser(newUser);
        return "OpenFeign POST 调用成功: " + result;
    }

    @GetMapping("/feign/put")
    public String testFeignPut() {
        User updateUser = new User(201L, "优秀工程师", 25);
        String result = userFeignClient.updateUser(updateUser);
        return "OpenFeign PUT 调用成功: " + result;
    }

    @GetMapping("/feign/delete")
    public String testFeignDelete() {
        String result = userFeignClient.deleteUser(201L);
        return "OpenFeign DELETE 调用成功: " + result;
    }
}