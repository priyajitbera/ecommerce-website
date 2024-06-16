package com.priyajit.ecommerce.product.catalog.service.model;

import com.priyajit.ecommerce.product.catalog.service.entity.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@RedisHash(value = "productModel", timeToLive = 3600) // 1 hour
public class ProductModel {

    @Id // to store & search in Redis
    private String id;
    private ZonedDateTime createdOn;
    private ZonedDateTime lastModifiedOn;
    private String title;
    private ProductPriceModel price;
    private String description;
    private List<ProductImageModel> images;
    private List<ProductCategoryModel> taggedCategories;


    @Data
    @Builder
    public static class ProductPriceModel {

        private BigDecimal price;
        private CurrencyModel currency;

        public static ProductPriceModel from(ProductPrice productPrice) {
            if (productPrice == null) return null;

            String currencyName = productPrice.getCurrency() == null ? null :
                    productPrice.getCurrency().getId();

            return ProductPriceModel.builder()
                    .price(productPrice.getPrice())
                    .currency(CurrencyModel.buildFrom(productPrice.getCurrency()))
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
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageModel {
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

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCategoryModel {
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

        ProductPriceModel productPrice = ProductPriceModel.from(product.getPrice());

        return ProductModel.builder()
                .id(product.getId().toString())
                .createdOn(product.getCreatedOn())
                .lastModifiedOn(product.getLastModifiedOn())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(productPrice)
                .images(productImages)
                .taggedCategories(taggedCategories)
                .build();
    }
}
