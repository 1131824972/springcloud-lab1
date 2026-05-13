package com.ynu.lab.lab2.provider.controller;

import com.ynu.lab.lab2.provider.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Value("${server.port}")
    private String port;
    // 1. GET 请求：查询数据 (通过 URL 路径传参)
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        System.out.println("提供者：收到查询用户请求，ID = " + id);
        return new User(id, "云南大学学生(提供者端口：" + port + ")", 21);
    }

    // 2. POST 请求：新增数据 (通过请求体传参)
    @PostMapping("/add")
    public String addUser(@RequestBody User user) {
        System.out.println("提供者：收到新增用户请求，数据 = " + user.toString());
        return "新增成功: " + user.getName();
    }

    // 3. PUT 请求：修改数据 (通过请求体传参)
    @PutMapping("/update")
    public String updateUser(@RequestBody User user) {
        System.out.println("提供者：收到修改用户请求，数据 = " + user.toString());
        return "修改成功，新年龄为: " + user.getAge();
    }

    // 4. DELETE 请求：删除数据 (通过 URL 路径传参)
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        System.out.println("提供者：收到删除用户请求，ID = " + id);
        return "删除成功，ID: " + id;
    }
}