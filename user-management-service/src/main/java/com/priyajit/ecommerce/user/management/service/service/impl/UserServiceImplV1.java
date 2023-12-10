package com.priyajit.ecommerce.user.management.service.service.impl;

import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.service.repository.UserRepository;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImplV1 implements UserService {

    private UserRepository userRepository;

    public UserServiceImplV1(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> findUsers(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    @Override
    public List<User> createUsers(List<CreateUserDto> dtoList) {

        List<User> userList = dtoList.stream()
                .map(this::createUserFromDto)
                .collect(Collectors.toList());

        return userRepository.saveAllAndFlush(userList);
    }

    private User createUserFromDto(CreateUserDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }
}
