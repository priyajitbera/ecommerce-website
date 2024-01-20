package com.priyajit.ecommerce.user.management.service.service.impl;

import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.model.CreateUserModel;
import com.priyajit.ecommerce.user.management.model.FindUserModel;
import com.priyajit.ecommerce.user.management.repository.UserRepository;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class UserServiceImplV1 implements UserService {

    private UserRepository userRepository;

    public UserServiceImplV1(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Method to find user using ids
     *
     * @param userIds
     * @return
     */
    @Override
    public List<FindUserModel> findUsers(List<BigInteger> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .map(FindUserModel::buildFrom)
                .collect(Collectors.toList());
    }

    /**
     * Method to create users
     *
     * @param dtoList
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CreateUserModel> createUsers(List<CreateUserDto> dtoList) {

        // create User objects from request dtos
        List<User> userList = dtoList.stream()
                .map(this::createUserFromDto)
                .collect(Collectors.toList());

        // save users
        List<User> savedUsers = userRepository.saveAllAndFlush(userList);

        // create response model and return
        return savedUsers.stream()
                .map(CreateUserModel::buildFrom)
                .collect(Collectors.toList());
    }

    /**
     * Helper method create User object from CreateUserDto
     *
     * @param dto DTO containing new user data
     * @return
     */
    private User createUserFromDto(CreateUserDto dto) {
        return User.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }
}
