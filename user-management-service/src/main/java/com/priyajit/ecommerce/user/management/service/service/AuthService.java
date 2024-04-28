package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.dto.LoginDto;
import com.priyajit.ecommerce.user.management.dto.RequestEmailVerificationSecretDto;
import com.priyajit.ecommerce.user.management.dto.SignupDto;
import com.priyajit.ecommerce.user.management.dto.VerifyEmailDto;
import com.priyajit.ecommerce.user.management.model.LoginModel;
import com.priyajit.ecommerce.user.management.model.RequestEmailVerificationSecretModel;
import com.priyajit.ecommerce.user.management.model.SignupModel;
import com.priyajit.ecommerce.user.management.model.VerifyEmailModel;

public interface AuthService {

    LoginModel login(LoginDto dto);
    SignupModel signup(SignupDto dto);
    RequestEmailVerificationSecretModel requestEmailVerificationSecret(RequestEmailVerificationSecretDto dto);

    VerifyEmailModel verifyUserEmail(VerifyEmailDto dto);
}
