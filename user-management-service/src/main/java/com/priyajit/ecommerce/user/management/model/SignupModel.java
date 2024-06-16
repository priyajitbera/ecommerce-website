package com.priyajit.ecommerce.user.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupModel {

    private BigInteger userId;
    private String emailId;
    private String name;
}
