package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.entity.User;

import java.util.List;

public interface UserService {
    List<User> findUsers(List<Long> userIds);

    List<User> createUsers(List<CreateUserDto> dtoList);
}
