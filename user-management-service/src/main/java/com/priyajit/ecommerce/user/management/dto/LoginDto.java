package com.priyajit.ecommerce.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    private BigInteger userId;
    private String password;
}
