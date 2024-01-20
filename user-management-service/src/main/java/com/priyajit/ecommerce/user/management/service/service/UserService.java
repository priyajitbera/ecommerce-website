package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.model.CreateUserModel;
import com.priyajit.ecommerce.user.management.model.FindUserModel;

import java.math.BigInteger;
import java.util.List;

public interface UserService {
    List<FindUserModel> findUsers(List<BigInteger> userIds);

    List<CreateUserModel> createUsers(List<CreateUserDto> dtoList);
}
