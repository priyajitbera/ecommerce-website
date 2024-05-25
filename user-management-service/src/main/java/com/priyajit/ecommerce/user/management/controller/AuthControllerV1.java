package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.dto.*;
import com.priyajit.ecommerce.user.management.exceptionhandler.MethodArgumentNotValidExceptionHandler;
import com.priyajit.ecommerce.user.management.model.*;
import com.priyajit.ecommerce.user.management.service.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(originPatterns = "*")
public class AuthControllerV1 implements MethodArgumentNotValidExceptionHandler {

    private AuthService authService;

    public AuthControllerV1(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginModel>> login(@Valid @RequestBody LoginDto dto) {
        return ControllerHelper.supplyResponse(() -> authService.login(dto), log);
    }


    @PostMapping("/signup")
    public ResponseEntity<Response<SignupModel>> signup(
            @Valid @RequestBody SignupDto dto
    ) {
        return ControllerHelper.supplyResponse(() -> authService.signup(dto), log);
    }

    @PostMapping("/request-email-verification-secret")
    ResponseEntity<Response<RequestEmailVerificationSecretModel>> requestEmailVerificationSecret(
            @RequestBody RequestEmailVerificationSecretDto dto
    ) {
        return ControllerHelper.supplyResponse(() -> authService.requestEmailVerificationSecret(dto), log);
    }

    @PostMapping("/verify-email")
    ResponseEntity<Response<VerifyEmailModel>> verifyEamil(
            @RequestBody VerifyEmailDto dto
    ) {
        return ControllerHelper.supplyResponse(() -> authService.verifyUserEmail(dto), log);
    }

    @GetMapping("/check-email-id-available")
    public ResponseEntity<Response<CheckEmailIdAvailableModel>> checkEmailIdAvailable(
            @RequestParam(name = "emailId") String emailId
    ) {
        return ControllerHelper.supplyResponse(() -> authService.checkEmailIdAvailable(emailId), log);
    }

    @PostMapping("/get-user-details-from-token")
    public ResponseEntity<Response<UserDetailsModel>> getUserDetailsFromUserToken(
            @Valid @RequestBody GetUserDetailsRequestDto dto
    ) {
        return ControllerHelper.supplyResponse(() -> authService.getUserDetailsFromUserToken(dto), log);
    }
}
