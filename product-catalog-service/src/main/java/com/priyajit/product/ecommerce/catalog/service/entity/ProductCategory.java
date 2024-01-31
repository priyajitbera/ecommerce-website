package com.priyajit.product.ecommerce.catalog.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.comparator.Comparators;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory implements Comparable<ProductCategory> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    private String name;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "FK__PRODUCT_CATEGORY_PARENT_CATEGORY_ID__01"))
    private ProductCategory parentCategory;

    @OneToMany(mappedBy = "parentCategory")
//    @OrderBy("id ASC")
    private SortedSet<ProductCategory> childCategories;

    @ManyToMany
    @JoinTable(
            name = "CATEGORY_PRODUCT_MAP",
            foreignKey = @ForeignKey(name = "FK__CATEGORY_PRODUCT_MAP__PRODUCT_CATEGORY_ID_01"),
            inverseForeignKey = @ForeignKey(name = "FK__CATEGORY_PRODUCT_MAP__PRODUCT_ID__01")
    )
    private List<Product> taggedProducts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategory that = (ProductCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public int compareTo(ProductCategory o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
