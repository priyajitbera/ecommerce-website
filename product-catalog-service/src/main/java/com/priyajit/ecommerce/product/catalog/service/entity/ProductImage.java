package com.priyajit.ecommerce.product.catalog.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.comparator.Comparators;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage implements Comparable<ProductImage> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private ZonedDateTime zonedDateTime;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    private String url;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK__PRODUCT_IMAGE__PRODUCT_ID__01"))
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductImage that = (ProductImage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ProductImage o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
