package com.priyajit.ecommerce.product.catalog.service.esdoc;

import com.priyajit.ecommerce.product.catalog.service.entity.*;
import lombok.*;
import org.springframework.util.comparator.Comparators;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
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
    private List<ProductImageDoc> images;
    private List<ProductCategoryDoc> taggedCategories;

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

        var productImages = product.getImages() == null ? null :
                product.getImages().stream()
                        .map(ProductImageDoc::buildFrom)
                        .collect(Collectors.toList());

        var taggedCategories = product.getTaggedCategories() == null ? null :
                product.getTaggedCategories().stream()
                        .map(ProductCategoryDoc::buildFrom)
                        .collect(Collectors.toList());

        return ProductDoc.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(ProductPriceDoc.buildFrom(product.getPrice()))
                .images(productImages)
                .taggedCategories(taggedCategories)
                .build();
    }

    @Override
    public int compareTo(ProductDoc o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductPriceDoc {
        private BigDecimal price;
        private CurrencyDoc currency;

        public static ProductPriceDoc buildFrom(ProductPrice productPrice) {
            if (productPrice == null) return null;
            return ProductPriceDoc.builder()
                    .price(productPrice.getPrice())
                    .currency(CurrencyDoc.buildFrom(productPrice.getCurrency()))
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductCategoryDoc {
        private String id;
        private String name;

        public static ProductCategoryDoc buildFrom(ProductCategory productCategory) {
            if (productCategory == null) return null;

            return ProductCategoryDoc.builder()
                    .id(productCategory.getId())
                    .name(productCategory.getName())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductImageDoc {

        private String id;
        private String url;

        public static ProductImageDoc buildFrom(ProductImage productImage) {
            if (productImage == null) return null;
            return ProductImageDoc.builder()
                    .id(productImage.getId())
                    .url(productImage.getUrl())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurrencyDoc {

        private String id;
        private String name;
        private String symbol;
        private String shortSymbol;

        public static CurrencyDoc buildFrom(Currency currency) {
            if (currency == null) return null;

            return CurrencyDoc.builder()
                    .id(currency.getId())
                    .name(currency.getName())
                    .symbol(currency.getSymbol())
                    .shortSymbol(currency.getShortSymbol())
                    .build();
        }
    }
}