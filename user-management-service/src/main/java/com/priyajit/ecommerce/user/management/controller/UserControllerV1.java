package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.model.FindUserModel;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@CrossOrigin("*")
public class UserControllerV1 {

    private UserService userService;

    public UserControllerV1(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/find-one")
    public ResponseEntity<FindUserModel> findUser(
            @RequestParam BigInteger userId
    ) {
        return ok(userService.findUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<FindUserModel>> findUsers(
            @RequestParam(name = "userIds") List<BigInteger> userIds
    ) {
        return ok(userService.findUsers(userIds));
    }

    @GetMapping("/by-email")
    public ResponseEntity<List<FindUserModel>> findUsersByEmailIds(
            @RequestParam(name = "emailIds") List<String> emailIdList
    ) {
        return ok(userService.findUsersByEmailIds(emailIdList));
    }
}
