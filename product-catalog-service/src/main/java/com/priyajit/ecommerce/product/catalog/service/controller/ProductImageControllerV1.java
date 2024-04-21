package com.priyajit.ecommerce.product.catalog.service.controller;

import com.priyajit.ecommerce.product.catalog.service.dto.UploadProductImagesDto;
import com.priyajit.ecommerce.product.catalog.service.model.Response;
import com.priyajit.ecommerce.product.catalog.service.model.UploadProductImagesModel;
import com.priyajit.ecommerce.product.catalog.service.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/product-image/v1")
public class ProductImageControllerV1 {

    @Autowired
    private ProductImageService productImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<UploadProductImagesModel>> uploadProductImages(
            @ModelAttribute UploadProductImagesDto dto
    ) {
        try {
            var model = productImageService.uploadProductImages(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<UploadProductImagesModel>builder()
                            .data(model)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<UploadProductImagesModel>builder()
                            .error(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<UploadProductImagesModel>builder()
                            .error(e.getMessage())
                            .build());
        }
    }
}
