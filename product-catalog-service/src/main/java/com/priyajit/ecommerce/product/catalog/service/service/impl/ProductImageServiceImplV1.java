package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.client.ProductImageStore;
import com.priyajit.ecommerce.product.catalog.service.dto.UploadProductImagesDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductImage;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.UploadProductImagesModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductImageRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductImageServiceImplV1 implements ProductImageService {

    private ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod;
    private ProductRepositoryQueryMethod productRepositoryQueryMethod;
    private ProductImageStore productImageStore;

    public ProductImageServiceImplV1(ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod, ProductRepositoryQueryMethod productRepositoryQueryMethod, ProductImageStore productImageStore) {
        this.productImageRepositoryQueryMethod = productImageRepositoryQueryMethod;
        this.productRepositoryQueryMethod = productRepositoryQueryMethod;
        this.productImageStore = productImageStore;
    }

    /**
     * Method to upload Product Images
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public UploadProductImagesModel uploadProductImages(UploadProductImagesDto dto) {

        Product product = dto.getProductId() == null ? null
                : productRepositoryQueryMethod.findById(dto.getProductId())
                .orElseThrow(ProductNotFoundException.supplier(dto.getProductId()));

        List<ProductImage> productImages = dto.getImageFiles()
                .stream()
                .map(file -> {
                            String key = String.format("%s-%s", UUID.randomUUID(), file.getOriginalFilename());
                            String url = uploadFile(key, file);
                            return ProductImage.builder()
                                    .url(url)
                                    .product(product)
                                    .build();
                        }
                )
                .collect(Collectors.toList());

        // save ProductImage objects
        productImageRepositoryQueryMethod.saveAllAndFlush(productImages);

        // create response model and return
        List<UploadProductImagesModel.ProductImageModel> productImageModels = productImages.stream()
                .map(UploadProductImagesModel.ProductImageModel::buildFrom)
                .collect(Collectors.toList());

        return UploadProductImagesModel.builder()
                .productId(product == null ? null : product.getId())
                .productImages(productImageModels)
                .build();
    }

    /**
     * Helper method to store a product image file
     *
     * @param key
     * @param file
     * @return
     */
    private String uploadFile(String key, MultipartFile file) {
        return productImageStore.store(key, file);
    }
}
