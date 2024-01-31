package com.priyajit.product.ecommerce.catalog.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.comparator.Comparators;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.SortedSet;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Comparable<Product> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    private String title;
    private String description;

    @OneToOne(mappedBy = "product", cascade = CascadeType.PERSIST)
    private ProductPrice price;

    @OneToMany(mappedBy = "product")
//    @OrderBy("id ASC")
    private SortedSet<ProductImage> images;

    @ManyToMany(mappedBy = "taggedProducts")
//    @OrderBy("id ASC")
    private SortedSet<ProductCategory> taggedCategories;

    @OneToMany(mappedBy = "product")
//    @OrderBy("id ASC")
    private SortedSet<ProductReview> reviews;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Product o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
