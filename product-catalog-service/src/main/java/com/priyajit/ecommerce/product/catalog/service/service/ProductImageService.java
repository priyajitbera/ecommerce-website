package com.priyajit.ecommerce.product.catalog.service.service;

import com.priyajit.ecommerce.product.catalog.service.dto.UploadProductImagesDto;
import com.priyajit.ecommerce.product.catalog.service.model.UploadProductImagesModel;

public interface ProductImageService {
    UploadProductImagesModel uploadProductImages(UploadProductImagesDto dto);
}
