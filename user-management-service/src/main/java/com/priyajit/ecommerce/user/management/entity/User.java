package com.priyajit.ecommerce.user.management.entity;

import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;

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
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    private String emailId;

    private String name;

    private EmailVerificationStatus emailVerificationStatus;

    private String emailVerificationSecret;
    private ZonedDateTime emailVerificationSecretGeneratedOn;

    private ZonedDateTime emailVerifiedOn;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST)
    private UserSecret userSecret;

    @ManyToMany
    @JoinTable(
            name = "USER_ROLE_MAP",
            foreignKey = @ForeignKey(name = "FK__USER_ROLE__MAP_USER_ID"),
            inverseForeignKey = @ForeignKey(name = "FK__USER_ROLE_MAP__ROLE_ID"),
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private List<Role> roles;
}
