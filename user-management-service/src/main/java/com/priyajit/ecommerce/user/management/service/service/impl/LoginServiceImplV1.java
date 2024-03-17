package com.priyajit.ecommerce.user.management.service.service.impl;

import com.priyajit.ecommerce.user.management.component.UserAuthTokenProvider;
import com.priyajit.ecommerce.user.management.dto.LoginDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import com.priyajit.ecommerce.user.management.entity.enums.LoginAttemptStatus;
import com.priyajit.ecommerce.user.management.model.LoginModel;
import com.priyajit.ecommerce.user.management.repository.UserRepository;
import com.priyajit.ecommerce.user.management.service.service.LoginService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImplV1 implements LoginService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserAuthTokenProvider userAuthTokenProvider;

    public LoginServiceImplV1(UserRepository userRepository, PasswordEncoder passwordEncoder, UserAuthTokenProvider userAuthTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthTokenProvider = userAuthTokenProvider;
    }

    @Override
    public LoginModel login(LoginDto dto) {
        // null validations
        if (dto == null) throw new IllegalArgumentException("Unexpected null value found for argument dto:LoginDto");
        if (dto.getUserId() == null || dto.getPassword() == null)
            throw new IllegalArgumentException("Unexpected null value found for argument userId:Long, password:String");

        // find user
        User user = userRepository.findById(dto.getUserId())
                .orElse(null);

        // if not user found with id
        if (user == null) {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("user-id or password is incorrect")
                    .build();
        }

        // validate email verification status
        if (EmailVerificationStatus.NOT_VERIFIED.equals(user.getEmailVerificationStatus())) {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("Email verification is not complete")
                    .build();
        }

        // if UserSecret entity is not created or correctly mapped for the user
        if (user.getUserSecret() == null || user.getUserSecret().getPassword() == null) {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("User onboarding is not complete or unsuccessful, contact System Administrator")
                    .build();
        }

        // match password
        if (passwordEncoder.matches(dto.getPassword(), user.getUserSecret().getPassword())) {
            String token = userAuthTokenProvider.generateToken(user.getId().toString());
            return LoginModel.builder()
                    .status(LoginAttemptStatus.SUCCESS)
                    .userId(user.getId())
                    .token(token)
                    .build();
        } else {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("UserId or Password is incorrect")
                    .build();
        }
    }
}
