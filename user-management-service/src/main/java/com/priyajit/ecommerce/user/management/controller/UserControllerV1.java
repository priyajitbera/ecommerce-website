package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.model.CreateUserModel;
import com.priyajit.ecommerce.user.management.model.FindUserModel;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/v1/user")
@CrossOrigin("*")
public class UserControllerV1 {

    private UserService userService;

    public UserControllerV1(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/find-one")
    FindUserModel findUser(
            @RequestParam BigInteger userId) {
        return userService.findUser(userId);
    }

    @GetMapping
    List<FindUserModel> findUsers(
            @RequestParam(name = "userIds") List<BigInteger> userIds
    ) {
        return userService.findUsers(userIds);
    }

    @GetMapping("/by-email")
    List<FindUserModel> findUsersByEmailIds(
            @RequestParam(name = "emailIds") List<String> emailIdList
    ) {
        return userService.findUsersByEmailIds(emailIdList);
    }

    @PostMapping
    List<CreateUserModel> createUser(
            @RequestBody List<CreateUserDto> dtoList
    ) {
        return userService.createUsers(dtoList);
    }
}
