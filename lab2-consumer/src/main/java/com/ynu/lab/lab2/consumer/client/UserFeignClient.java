package com.ynu.lab.lab2.consumer.client;


import com.ynu.lab.lab2.consumer.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "lab2-provider")
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    User getUserById(@PathVariable("id") Long id);

    @PostMapping("/user/add")
    String addUser(@RequestBody User user);

    @PutMapping("/user/update")
    String updateUser(@RequestBody User user);

    @DeleteMapping("/user/delete/{id}")
    String deleteUser(@PathVariable("id") Long id);
}
