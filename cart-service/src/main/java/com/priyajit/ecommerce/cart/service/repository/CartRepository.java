package com.priyajit.ecommerce.cart.service.repository;

import com.priyajit.ecommerce.cart.service.mogodoc.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);

    List<Cart> findAllByUserIdIn(List<String> userIds);
}
