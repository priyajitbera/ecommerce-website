package com.priyajit.order.management.service.mongorepository;

import com.priyajit.order.management.service.mongodoc.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByUserId(String userId, PageRequest of);

    Optional<Order> findByPaymentInfo_paymentId(String paymentId);
}
