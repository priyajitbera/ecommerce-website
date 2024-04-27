package com.priyajit.ecommerce.user.management.repository;

import com.priyajit.ecommerce.user.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {

    List<User> findAllByEmailIdIn(List<String> emailIdList);
    Optional<User> findByEmailId(String emailId);
}