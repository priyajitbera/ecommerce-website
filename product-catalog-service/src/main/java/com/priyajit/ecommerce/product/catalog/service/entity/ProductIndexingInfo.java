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
public class ProductIndexingInfo implements Comparable<ProductIndexingInfo> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    private Boolean isIndexedOnElasticSearch;
    private ZonedDateTime elasticSearchIndexedOn;
    private String indexedByUserId;

    @OneToOne
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "FK__PRODUCT_INDEXING_INFO__PRODUCT_ID__01")
    )
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductIndexingInfo product = (ProductIndexingInfo) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ProductIndexingInfo o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
