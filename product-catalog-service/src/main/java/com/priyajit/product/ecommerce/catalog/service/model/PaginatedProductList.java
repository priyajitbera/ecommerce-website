package com.priyajit.product.ecommerce.catalog.service.model;

import com.priyajit.product.ecommerce.catalog.service.entity.Product;
import com.priyajit.product.ecommerce.catalog.service.entity.ProductCategory;
import com.priyajit.product.ecommerce.catalog.service.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedProductList {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductModel {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class ProductImageModel {
            private String id;
            private String url;

            public static ProductImageModel from(ProductImage productImage) {
                if (productImage == null) return null;

                return ProductImageModel.builder()
                        .id(productImage.getId().toString())
                        .url(productImage.getUrl())
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class ProductCategoryModel {
            private String id;
            private String name;

            public static ProductCategoryModel from(ProductCategory productCategory) {
                if (productCategory == null) return null;

                return ProductCategoryModel
                        .builder()
                        .id(productCategory.getId().toString())
                        .name(productCategory.getName())
                        .build();
            }
        }

        private String id;
        private ZonedDateTime createdOn;
        private ZonedDateTime lastModifiedOn;
        private String title;
        private Double price;
        private String description;
        private List<ProductImageModel> images;
        private List<ProductCategoryModel> taggedCategories;

        public static ProductModel from(Product product) {
            if (product == null) return null;

            List<ProductImageModel> productImages = product.getImages() == null ? null :
                    product.getImages().stream()
                            .map(ProductImageModel::from)
                            .collect(Collectors.toList());

            List<ProductCategoryModel> taggedCategories = product.getTaggedCategories() == null ? null :
                    product.getTaggedCategories().stream()
                            .map(ProductCategoryModel::from)
                            .collect(Collectors.toList());

            return builder()
                    .id(product.getId().toString())
                    .createdOn(product.getCreatedOn())
                    .lastModifiedOn(product.getLastModifiedOn())
                    .title(product.getTitle())
                    .description(product.getDescription())
                    .images(productImages)
                    .taggedCategories(taggedCategories)
                    .build();
        }
    }

    private List<ProductModel> products;
    private Integer totalPages;
    private Long totalElements;
    private Integer size;
    private Integer number;

    public static PaginatedProductList from(Page<Product> productPage) {

        List<ProductModel> products = productPage.get()
                .map(ProductModel::from)
                .collect(Collectors.toList());

        return PaginatedProductList.builder()
                .products(products)
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .size(productPage.getSize())
                .number(productPage.getNumber())
                .build();
    }
}
