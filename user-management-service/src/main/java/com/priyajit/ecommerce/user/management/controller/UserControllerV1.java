package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user")
public class UserControllerV1 {

    private UserService userService;

    public UserControllerV1(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    List<User> findUsers(
            @RequestParam(name = "userIds") List<Long> userIds
    ) {
        return userService.findUsers(userIds);
    }

    @PostMapping
    List<User> createUser(
            @RequestBody List<CreateUserDto> dtoList
    ) {
        return userService.createUsers(dtoList);
    }
}
