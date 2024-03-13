package com.priyajit.ecommerce.product.catalog.service.esdoc;

import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import lombok.*;
import org.springframework.util.comparator.Comparators;

import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDoc implements Comparable<ProductDoc> {


    private String id;
    private String title;
    private String description;
    private ProductPriceDoc price;
    private SortedSet<ProductImageDoc> images;
    private SortedSet<ProductCategoryDoc> taggedCategories;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDoc product = (ProductDoc) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static List<ProductDoc> buildFrom(List<Product> products) {
        if (products == null) return null;
        else return products.stream()
                .map(ProductDoc::buildFrom)
                .collect(Collectors.toList());
    }

    public static ProductDoc buildFrom(Product product) {
        return ProductDoc.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(ProductPriceDoc.buildFrom(product.getPrice()))
                .images(ProductImageDoc.buildFrom(product.getImages()))
                .taggedCategories(ProductCategoryDoc.buildFrom(product.getTaggedCategories()))
                .build();
    }

    @Override
    public int compareTo(ProductDoc o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
