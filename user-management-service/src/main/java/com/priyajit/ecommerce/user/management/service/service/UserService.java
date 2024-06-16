package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.model.FindUserModel;

import java.math.BigInteger;
import java.util.List;

public interface UserService {

    FindUserModel findUser(BigInteger userId);

    List<FindUserModel> findUsers(List<BigInteger> userIds);

    List<FindUserModel> findUsersByEmailIds(List<String> emailIdList);
}
