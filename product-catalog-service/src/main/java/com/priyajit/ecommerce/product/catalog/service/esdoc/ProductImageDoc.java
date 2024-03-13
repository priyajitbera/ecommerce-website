package com.priyajit.ecommerce.product.catalog.service.esdoc;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductImage;
import lombok.*;
import org.springframework.util.comparator.Comparators;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDoc implements Comparable<ProductImageDoc> {

    private String id;
    private String url;

    public static SortedSet<ProductImageDoc> buildFrom(SortedSet<ProductImage> images) {
        if (images == null) return null;
        return images.stream()
                .map(ProductImageDoc::buildFrom)
                .collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    public static ProductImageDoc buildFrom(ProductImage productImage) {
        if (productImage == null) return null;
        return ProductImageDoc.builder()
                .id(productImage.getId())
                .url(productImage.getUrl())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductImageDoc product = (ProductImageDoc) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ProductImageDoc o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
