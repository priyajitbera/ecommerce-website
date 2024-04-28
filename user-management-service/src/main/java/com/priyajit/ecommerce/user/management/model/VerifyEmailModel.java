package com.priyajit.ecommerce.user.management.model;

import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEmailModel {

    private BigInteger userId;
    private String emailId;
    private EmailVerificationStatus verificationStatus;
    private ZonedDateTime verifiedOn;
}
