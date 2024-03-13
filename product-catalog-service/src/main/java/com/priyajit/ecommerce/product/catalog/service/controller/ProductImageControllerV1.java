package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.UploadProductImagesDto;
import com.priyajit.ecommerce.product.catalog.service.model.UploadProductImagesModel;
import com.priyajit.ecommerce.product.catalog.service.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-image/v1")
public class ProductImageControllerV1 {

    @Autowired
    private ProductImageService productImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadProductImagesModel uploadProductImages(
            @ModelAttribute UploadProductImagesDto dto
    ) {
        return productImageService.uploadProductImages(dto);
    }
}
