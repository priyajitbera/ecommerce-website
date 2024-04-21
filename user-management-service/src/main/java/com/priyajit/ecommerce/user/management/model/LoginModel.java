package com.priyajit.ecommerce.user.management.model;

import com.priyajit.ecommerce.user.management.entity.enums.LoginAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginModel {

    private LoginAttemptStatus status;
    private String message;
    private BigInteger userId;
    private List<String> roles;
    private String token;
}
