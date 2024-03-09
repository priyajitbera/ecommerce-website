package com.priyajit.ecommerce.product.catalog.service.model;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadProductImagesModel {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageModel {
        private String id;
        private String url;

        public static ProductImageModel buildFrom(ProductImage productImage) {
            return ProductImageModel.builder()
                    .id(productImage.getId())
                    .url(productImage.getUrl())
                    .build();
        }
    }

    private String productId;
    private List<ProductImageModel> productImages;
}
