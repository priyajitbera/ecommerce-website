package com.priyajit.ecommerce.product.catalog.service.esdoc;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import lombok.*;
import org.springframework.util.comparator.Comparators;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDoc implements Comparable<ProductCategoryDoc> {

    private String id;
    private String name;
    private ProductCategoryDoc parentCategory;

    private SortedSet<ProductCategoryDoc> childCategories;

    public static SortedSet<ProductCategoryDoc> buildFrom(SortedSet<ProductCategory> taggedCategories) {
        if (taggedCategories == null) return null;
        else return taggedCategories.stream()
                .map(ProductCategoryDoc::buildFrom)
                .collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    public static ProductCategoryDoc buildFrom(ProductCategory productCategory) {
        if (productCategory == null) return null;
        return ProductCategoryDoc.builder()
                .id(productCategory.getId())
                .name(productCategory.getName())
                .parentCategory(ProductCategoryDoc.buildFrom(productCategory.getParentCategory()))
                .childCategories(ProductCategoryDoc.buildFrom(productCategory.getChildCategories()))
                .build();
    }

    @Override
    public int compareTo(ProductCategoryDoc o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
