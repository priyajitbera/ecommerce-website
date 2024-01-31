package com.priyajit.product.ecommerce.catalog.service.model;

import com.priyajit.product.ecommerce.catalog.service.entity.ProductCategory;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
public class ProductCategoryModel {

    @Data
    @Builder
    public static class ParentCategoryModel {
        private String id;
        private String name;

        public static ParentCategoryModel from(ProductCategory productCategory) {
            if (productCategory == null) return null;

            return ParentCategoryModel.builder()
                    .id(productCategory.getId())
                    .name(productCategory.getName())
                    .build();
        }
    }

    @Data
    @Builder
    public static class ChildCategoryModel {
        private String id;
        private String name;

        public static ChildCategoryModel from(ProductCategory productCategory) {
            if (productCategory == null) return null;

            return ChildCategoryModel.builder()
                    .id(productCategory.getId())
                    .name(productCategory.getName())
                    .build();
        }

        public static List<ChildCategoryModel> from(Collection<ProductCategory> productCategories) {
            if (productCategories == null) return null;

            return productCategories.stream()
                    .filter(Objects::nonNull)
                    .map(ChildCategoryModel::from)
                    .collect(Collectors.toList());
        }
    }

    private String id;
    private ZonedDateTime createdOn;
    private ZonedDateTime lastModifiedOn;
    private String name;
    private ParentCategoryModel parentCategory;
    private List<ChildCategoryModel> childCategories;

    public static ProductCategoryModel from(ProductCategory productCategory) {
        if (productCategory == null) return null;

        List<ChildCategoryModel> childCategories = ChildCategoryModel.from(productCategory.getChildCategories());

        return ProductCategoryModel.builder()
                .id(productCategory.getId())
                .createdOn(productCategory.getCreatedOn())
                .lastModifiedOn(productCategory.getLastModifiedOn())
                .name(productCategory.getName())
                .parentCategory(ParentCategoryModel.from(productCategory.getParentCategory()))
                .childCategories(childCategories)
                .build();
    }
}
