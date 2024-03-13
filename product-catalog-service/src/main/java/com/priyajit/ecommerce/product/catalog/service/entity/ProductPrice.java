package com.priyajit.ecommerce.product.catalog.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK__PRODUCT_PRICE__PRODUCT_ID__01"))
    private Product product;

    @ManyToOne
    private Currency currency;

    private long price; // stores as (real-price*100), e.g. 10.12 saved as 1012, support till two decimal place
}
