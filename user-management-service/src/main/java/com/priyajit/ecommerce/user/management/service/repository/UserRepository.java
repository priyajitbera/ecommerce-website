package com.priyajit.ecommerce.user.management.service.repository;

import com.priyajit.ecommerce.user.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
