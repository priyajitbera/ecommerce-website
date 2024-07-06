package com.priyajit.ecommerce.product.catalog.service.model;

import com.priyajit.ecommerce.product.catalog.service.entity.*;
import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductDoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedProductList {

    private List<ProductModel> products;
    private Integer totalPages;
    private Long totalElements;
    private Integer size;
    private Integer number;

    public static PaginatedProductList buildFrom(Page<ProductDoc> page) {
        return PaginatedProductList.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .number(page.getNumber())
                .products(ProductModel.buildFrom(page.getContent()))
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductModel {

        private String id;
        private ZonedDateTime createdOn;
        private ZonedDateTime lastModifiedOn;
        private String title;
        private ProductPriceModel price;
        private String description;
        private List<ProductImageModel> images;
        private List<ProductCategoryModel> taggedCategories;
        private ProductIndexingInfoModel productIndexingInfo;

        public static List<ProductModel> buildFrom(List<ProductDoc> productDocs) {
            if (productDocs == null) return null;
            return productDocs.stream().map(ProductModel::buildFrom).collect(Collectors.toList());
        }

        public static ProductModel buildFrom(ProductDoc productDoc) {

            var productImages = productDoc.getImages() == null ? null
                    : productDoc.getImages().stream().map(ProductImageModel::from)
                    .collect(Collectors.toList());

            var taggedCategories = productDoc.getTaggedCategories() == null ? null
                    : productDoc.getTaggedCategories().stream()
                    .map(ProductCategoryModel::from)
                    .collect(Collectors.toList());

            return ProductModel.builder()
                    .id(productDoc.getId())
                    .title(productDoc.getTitle())
                    .description(productDoc.getDescription())
                    .price(ProductPriceModel.from(productDoc.getPrice()))
                    .images(productImages)
                    .taggedCategories(taggedCategories)
                    .build();
        }


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

            public static ProductImageModel from(ProductDoc.ProductImageDoc productImage) {
                if (productImage == null) return null;

                return ProductImageModel.builder()
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
                        .id(productCategory.getId())
                        .name(productCategory.getName())
                        .build();
            }

            public static ProductCategoryModel from(ProductDoc.ProductCategoryDoc productCategory) {
                if (productCategory == null) return null;

                return ProductCategoryModel
                        .builder()
                        .id(productCategory.getId())
                        .name(productCategory.getName())
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        private static class ProductPriceModel {

            private BigDecimal price;
            private CurrencyModel currency;

            public static ProductPriceModel from(ProductPrice productPrice) {
                if (productPrice == null) return null;
                return ProductPriceModel.builder()
                        .price(productPrice.getPrice())
                        .currency(CurrencyModel.buildFrom(productPrice.getCurrency()))
                        .build();
            }

            public static ProductPriceModel from(ProductDoc.ProductPriceDoc price) {
                if (price == null) return null;
                return ProductPriceModel.builder()
                        .price(price.getPrice())
                        .currency(CurrencyModel.buildFrom(price.getCurrency()))
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CurrencyModel {

            private String id;
            private String name;
            private String symbol;
            private String shortSymbol;

            public static CurrencyModel buildFrom(Currency currency) {
                if (currency == null) return null;

                return CurrencyModel.builder()
                        .id(currency.getId())
                        .name(currency.getName())
                        .symbol(currency.getSymbol())
                        .shortSymbol(currency.getShortSymbol())
                        .build();
            }

            public static CurrencyModel buildFrom(ProductDoc.CurrencyDoc currency) {
                if (currency == null) return null;
                return CurrencyModel.builder()
                        .id(currency.getId())
                        .name(currency.getName())
                        .symbol(currency.getSymbol())
                        .shortSymbol(currency.getShortSymbol())
                        .build();
            }
        }


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
                    .price(ProductPriceModel.from(product.getPrice()))
                    .productIndexingInfo(ProductIndexingInfoModel.buildFrom(product.getProductIndexingInfo()))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductIndexingInfoModel {
        private Boolean isIndexedOnElasticSearch;
        private ZonedDateTime elasticSearchIndexedOn;

        public static ProductIndexingInfoModel buildFrom(ProductIndexingInfo productIndexingInfo) {
            if (productIndexingInfo == null) return null;

            return ProductIndexingInfoModel.builder()
                    .isIndexedOnElasticSearch(productIndexingInfo.getIsIndexedOnElasticSearch())
                    .elasticSearchIndexedOn(productIndexingInfo.getElasticSearchIndexedOn())
                    .build();
        }
    }


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
