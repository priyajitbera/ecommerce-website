package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.UploadProductImagesDto;
import com.priyajit.ecommerce.product.catalog.service.model.UploadProductImagesModel;
import com.priyajit.ecommerce.product.catalog.service.service.ProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/product-image")
@CrossOrigin("*")
public class ProductImageControllerV1 {

    @Autowired
    private ProductImageService productImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadProductImagesModel> uploadProductImages(
            @ModelAttribute UploadProductImagesDto dto
    ) {
        return ok(productImageService.uploadProductImages(dto));
    }
}
