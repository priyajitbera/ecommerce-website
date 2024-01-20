package com.priyajit.ecommerce.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailDto {

    private BigInteger userId;
    private String emailVerificationSecret;
}
