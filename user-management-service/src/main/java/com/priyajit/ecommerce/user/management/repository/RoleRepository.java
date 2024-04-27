package com.priyajit.ecommerce.user.management.repository;

import com.priyajit.ecommerce.user.management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, BigInteger> {
    List<Role> findAllByNameIn(Set<String> roles);
}
