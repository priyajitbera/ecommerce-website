package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.dto.LoginDto;
import com.priyajit.ecommerce.user.management.dto.SignupDto;
import com.priyajit.ecommerce.user.management.model.LoginModel;
import com.priyajit.ecommerce.user.management.model.Response;
import com.priyajit.ecommerce.user.management.model.SignupModel;
import com.priyajit.ecommerce.user.management.service.service.LoginService;
import com.priyajit.ecommerce.user.management.service.service.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(originPatterns = "*")
public class AuthControllerV1 {

    private LoginService loginService;
    private SignupService signUpService;

    public AuthControllerV1(LoginService loginService, SignupService signUpService) {
        this.loginService = loginService;
        this.signUpService = signUpService;
    }

    @PostMapping("/login")
    public LoginModel login(@RequestBody LoginDto dto) {
        return loginService.login(dto);
    }

    @PostMapping("/signup")
    public ResponseEntity<Response<SignupModel>> signup(
            @RequestBody SignupDto dto
    ) {
        try {
            var model = signUpService.signup(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<SignupModel>builder().data(model).build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<SignupModel>builder().error(e.getReason()).build());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<SignupModel>builder().build());
        }
    }
}
