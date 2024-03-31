package com.priyajit.order.management.service.client;

import com.priyajit.order.management.service.client.model.UserModel;

import java.math.BigInteger;
import java.util.Optional;

public interface UserManagementServiceClient {
    Optional<UserModel> findUserByUserId(String userId);
}
