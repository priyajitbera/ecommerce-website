package com.priyajit.ecommerce.user.management.entity;

import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    @CreationTimestamp
    private ZonedDateTime createOn;

    @UpdateTimestamp
    private ZonedDateTime lastModified;

    @Column(unique = true)
    private String emailId;

    private String name;

    private EmailVerificationStatus emailVerificationStatus;

    private String emailVerificationSecret;
    private ZonedDateTime emailVerificationSecretGeneratedOn;

    private ZonedDateTime emailVerifiedOn;
}
