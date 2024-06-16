package com.priyajit.ecommerce.user.management.repository;

import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {

    List<User> findAllByEmailIdInAndEmailVerificationStatus(List<String> emailIdList, EmailVerificationStatus emailVerificationStatus);
    Optional<User> findByEmailId(String emailId);

    Optional<User> findByEmailIdAndEmailVerificationStatus(String emailId, EmailVerificationStatus emailVerificationStatus);
}