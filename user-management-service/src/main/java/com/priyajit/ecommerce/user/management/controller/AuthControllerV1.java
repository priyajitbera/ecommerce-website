package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.dto.LoginDto;
import com.priyajit.ecommerce.user.management.dto.RequestEmailVerificationSecretDto;
import com.priyajit.ecommerce.user.management.dto.SignupDto;
import com.priyajit.ecommerce.user.management.dto.VerifyEmailDto;
import com.priyajit.ecommerce.user.management.model.*;
import com.priyajit.ecommerce.user.management.service.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(originPatterns = "*")
public class AuthControllerV1 {

    private AuthService authService;

    public AuthControllerV1(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginModel login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/signup")
    public ResponseEntity<Response<SignupModel>> signup(
            @RequestBody SignupDto dto
    ) {
        try {
            var model = authService.signup(dto);
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

    @PostMapping("/request-email-verification-secret")
    ResponseEntity<Response<RequestEmailVerificationSecretModel>> requestEmailVerificationSecret(
            @RequestBody RequestEmailVerificationSecretDto dto
    ) {
        try {
            var model = authService.requestEmailVerificationSecret(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<RequestEmailVerificationSecretModel>builder().data(model).build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<RequestEmailVerificationSecretModel>builder().error(e.getReason()).build());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<RequestEmailVerificationSecretModel>builder().build());
        }
    }

    @PostMapping("/verify-email")
    ResponseEntity<Response<VerifyEmailModel>> verifyEamil(
            @RequestBody VerifyEmailDto dto
    ) {
        try {
            var model = authService.verifyUserEmail(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<VerifyEmailModel>builder().data(model).build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<VerifyEmailModel>builder().error(e.getReason()).build());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<VerifyEmailModel>builder().build());
        }
    }

    @GetMapping("/check-email-id-available")
    public ResponseEntity<Response<CheckEmailIdAvailableModel>> checkEmailIdAvailable(
            @RequestParam(name = "emailId") String emailId
    ) {
        try {
            var model = authService.checkEmailIdAvailable(emailId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<CheckEmailIdAvailableModel>builder().data(model).build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<CheckEmailIdAvailableModel>builder().error(e.getReason()).build());
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<CheckEmailIdAvailableModel>builder().build());
        }
    }
}
