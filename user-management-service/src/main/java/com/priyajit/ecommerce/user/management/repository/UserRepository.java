package com.priyajit.ecommerce.user.management.repository;

import com.priyajit.ecommerce.user.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {

    List<User> findAllByEmailIn(List<String> emailIdList);
}