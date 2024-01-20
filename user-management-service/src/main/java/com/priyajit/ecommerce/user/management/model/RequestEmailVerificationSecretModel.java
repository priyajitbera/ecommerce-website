package com.priyajit.ecommerce.user.management.model;

import com.priyajit.ecommerce.user.management.entity.enums.RequestEmailVerifcationSecretStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class RequestEmailVerificationSecretModel {

    private BigInteger userId;
    private RequestEmailVerifcationSecretStatus status;
    private String message;
}
