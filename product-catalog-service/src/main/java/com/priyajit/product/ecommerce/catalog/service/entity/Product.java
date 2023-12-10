package com.priyajit.product.ecommerce.catalog.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private ZonedDateTime zonedDateTime;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;

    @OneToOne(mappedBy = "product")
    private ProductRating rating;
}
