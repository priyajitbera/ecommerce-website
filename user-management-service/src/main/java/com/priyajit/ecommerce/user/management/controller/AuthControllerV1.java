package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.dto.LoginDto;
import com.priyajit.ecommerce.user.management.model.LoginModel;
import com.priyajit.ecommerce.user.management.service.service.LoginService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(originPatterns = "*")
public class AuthControllerV1 {

    private LoginService loginService;

    public AuthControllerV1(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public LoginModel login(@RequestBody LoginDto dto) {
        return loginService.login(dto);
    }

}
