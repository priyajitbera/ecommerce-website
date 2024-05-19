package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.model.FindUserModel;
import com.priyajit.ecommerce.user.management.model.Response;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

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
    public ResponseEntity<Response<FindUserModel>> findUser(
            @RequestParam BigInteger userId) {

        return ControllerHelper.supplyResponse(() -> userService.findUser(userId), log);
    }

    @GetMapping
    public ResponseEntity<Response<List<FindUserModel>>> findUsers(
            @RequestParam(name = "userIds") List<BigInteger> userIds
    ) {
        return ControllerHelper.supplyResponse(() -> userService.findUsers(userIds), log);
    }

    @GetMapping("/by-email")
    public ResponseEntity<Response<List<FindUserModel>>> findUsersByEmailIds(
            @RequestParam(name = "emailIds") List<String> emailIdList
    ) {
        return ControllerHelper.supplyResponse(() -> userService.findUsersByEmailIds(emailIdList), log);
    }
}
