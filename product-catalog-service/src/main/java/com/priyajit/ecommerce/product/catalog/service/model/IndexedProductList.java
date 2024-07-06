package com.priyajit.ecommerce.product.catalog.service.model;

import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductIndexingInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexedProductList {

    private List<IndexedProductModel> indexedProducts;

    public static IndexedProductList buildFrom(List<Product> products) {
        var indexedProducts = products == null ? null :
                products.stream()
                        .map(IndexedProductModel::buildFrom)
                        .collect(Collectors.toList());

        return IndexedProductList.builder()
                .indexedProducts(indexedProducts)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexedProductModel {

        private String id;
        private ProductIndexingInfoModel productIndexingInfo;

        public static IndexedProductModel buildFrom(Product product) {
            if (product == null) return null;

            return IndexedProductModel.builder()
                    .id(product.getId())
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
}
