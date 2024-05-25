package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.dto.*;
import com.priyajit.ecommerce.user.management.model.*;

public interface AuthService {

    LoginModel login(LoginDto dto);

    SignupModel signup(SignupDto dto);

    RequestEmailVerificationSecretModel requestEmailVerificationSecret(RequestEmailVerificationSecretDto dto);

    VerifyEmailModel verifyUserEmail(VerifyEmailDto dto);

    CheckEmailIdAvailableModel checkEmailIdAvailable(String emailId);

    UserDetailsModel getUserDetailsFromUserToken(GetUserDetailsRequestDto dto);
}
