package com.priyajit.ecommerce.user.management.repository;

import com.priyajit.ecommerce.user.management.domain.RoleName;
import com.priyajit.ecommerce.user.management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, BigInteger> {

    Optional<Role> findByName(RoleName name);
}
