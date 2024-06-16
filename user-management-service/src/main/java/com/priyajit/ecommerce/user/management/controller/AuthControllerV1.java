package com.priyajit.ecommerce.user.management.controller;

import com.priyajit.ecommerce.user.management.dto.*;
import com.priyajit.ecommerce.user.management.model.*;
import com.priyajit.ecommerce.user.management.service.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(originPatterns = "*")
public class AuthControllerV1 {

    private AuthService authService;

    public AuthControllerV1(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginModel> login(@Valid @RequestBody LoginDto dto) {
        return ok(authService.login(dto));
    }


    @PostMapping("/signup")
    public ResponseEntity<SignupModel> signup(
            @Valid @RequestBody SignupDto dto
    ) {
        return ok(authService.signup(dto));
    }

    @PostMapping("/request-email-verification-secret")
    ResponseEntity<RequestEmailVerificationSecretModel> requestEmailVerificationSecret(
            @RequestBody RequestEmailVerificationSecretDto dto
    ) {
        return ok(authService.requestEmailVerificationSecret(dto));
    }

    @PostMapping("/verify-email")
    ResponseEntity<VerifyEmailModel> verifyEamil(
            @RequestBody VerifyEmailDto dto
    ) {
        return ok(authService.verifyUserEmail(dto));
    }

    @GetMapping("/check-email-id-available")
    public ResponseEntity<CheckEmailIdAvailableModel> checkEmailIdAvailable(
            @RequestParam(name = "emailId") String emailId
    ) {
        return ok(authService.checkEmailIdAvailable(emailId));
    }

    @PostMapping("/get-user-details-from-token")
    public ResponseEntity<UserDetailsModel> getUserDetailsFromUserToken(
            @Valid @RequestBody GetUserDetailsRequestDto dto
    ) {
        return ok(authService.getUserDetailsFromUserToken(dto));
    }
}
