package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.dto.RequestEmailVerificationSecretDto;
import com.priyajit.ecommerce.user.management.dto.VerifyEmailDto;
import com.priyajit.ecommerce.user.management.model.CreateUserModel;
import com.priyajit.ecommerce.user.management.model.FindUserModel;
import com.priyajit.ecommerce.user.management.model.RequestEmailVerificationSecretModel;
import com.priyajit.ecommerce.user.management.model.VerifyEmailModel;

import java.math.BigInteger;
import java.util.List;

public interface UserService {
    FindUserModel findUser(BigInteger userId);

    List<FindUserModel> findUsers(List<BigInteger> userIds);

    List<CreateUserModel> createUsers(List<CreateUserDto> dtoList);

    RequestEmailVerificationSecretModel requestEmailVerificationSecret(RequestEmailVerificationSecretDto dto);

    VerifyEmailModel verifyUserEmail(VerifyEmailDto dto);

    List<FindUserModel> findUsersByEmailIds(List<String> emailIdList);
}
