package com.priyajit.ecommerce.product.catalog.service.service.impl;

import com.priyajit.ecommerce.product.catalog.service.client.ProductImageStore;
import com.priyajit.ecommerce.product.catalog.service.dto.UploadProductImagesDto;
import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import com.priyajit.ecommerce.product.catalog.service.entity.ProductImage;
import com.priyajit.ecommerce.product.catalog.service.exception.ProductNotFoundException;
import com.priyajit.ecommerce.product.catalog.service.model.UploadProductImagesModel;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductImageRepositoryQueryMethod;
import com.priyajit.ecommerce.product.catalog.service.repository.querymethod.ProductRepositoryQueryMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.when;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceV1ImplTest {

    @Mock
    private ProductRepositoryQueryMethod productRepositoryQueryMethod;

    @Mock
    private ProductImageRepositoryQueryMethod productImageRepositoryQueryMethod;

    @Mock
    private ProductImageStore productImageStore;

    @InjectMocks
    private ProductImageServiceImplV1 productImageService;

    @Test
    void uploadProductImages_success() {

        // arrange
        var productId = "PRODUCT-ID-1";
        var product = Product.builder().id(productId).build();
        var fileName1 = "FILE-NAME-1.jpg";
        var fileName2 = "FILE-NAME-2.png";
        var imageFile1 = new MockMultipartFile(fileName1, fileName1, "jpg", fileName1.getBytes());
        var imageFile2 = new MockMultipartFile(fileName1, fileName1, "png", fileName2.getBytes());
        List<MultipartFile> fileList = List.of(imageFile1, imageFile2);

        UploadProductImagesDto dto = UploadProductImagesDto.builder()
                .productId(productId)
                .imageFiles(fileList)
                .build();

        // mock
        when(productRepositoryQueryMethod.findById(productId))
                .thenReturn(Optional.of(product));
        when(productImageStore.store(any(String.class), any(MultipartFile.class)))
                .then(i -> mockUrl(i.getArgument(0, String.class), i.getArgument(1, MultipartFile.class)));
        when(productImageRepositoryQueryMethod.saveAllAndFlush(any(List.class)))
                .then(i -> mockSave(i.getArgument(0, List.class)));

        // act
        UploadProductImagesModel responseModel = productImageService.uploadProductImages(dto);

        // assert
        assertNotNull(responseModel);
        assertEquals(productId, responseModel.getProductId());
        assertEquals(2, responseModel.getProductImages().size());
        assertNotNull(responseModel.getProductImages().get(0).getId());
        assertNotNull(responseModel.getProductImages().get(1).getId());
        assertNotNull(responseModel.getProductImages().get(0).getUrl());
        assertNotNull(responseModel.getProductImages().get(1).getUrl());
    }

    @Test
    void uploadProductImages_withoutProductId_success() {

        // arrange
        var fileName1 = "FILE-NAME-1.jpg";
        var fileName2 = "FILE-NAME-2.png";
        var imageFile1 = new MockMultipartFile(fileName1, fileName1, "jpg", fileName1.getBytes());
        var imageFile2 = new MockMultipartFile(fileName1, fileName1, "png", fileName2.getBytes());
        List<MultipartFile> fileList = List.of(imageFile1, imageFile2);

        UploadProductImagesDto dto = UploadProductImagesDto.builder()
                .imageFiles(fileList)
                .build();

        // mock
        when(productImageStore.store(any(String.class), any(MultipartFile.class)))
                .then(i -> mockUrl(i.getArgument(0, String.class), i.getArgument(1, MultipartFile.class)));
        when(productImageRepositoryQueryMethod.saveAllAndFlush(any(List.class)))
                .then(i -> mockSave(i.getArgument(0, List.class)));

        // act
        UploadProductImagesModel responseModel = productImageService.uploadProductImages(dto);

        // assert
        assertNotNull(responseModel);
        assertEquals(2, responseModel.getProductImages().size());
        assertNotNull(responseModel.getProductImages().get(0).getId());
        assertNotNull(responseModel.getProductImages().get(1).getId());
        assertNotNull(responseModel.getProductImages().get(0).getUrl());
        assertNotNull(responseModel.getProductImages().get(1).getUrl());
    }

    @Test
    void uploadProductImages_withoutInvalidProductId_fail() {

        // arrange
        var productId = "INVALID-PRODUCT-ID";
        var fileName1 = "FILE-NAME-1.jpg";
        var fileName2 = "FILE-NAME-2.png";
        var imageFile1 = new MockMultipartFile(fileName1, fileName1, "jpg", fileName1.getBytes());
        var imageFile2 = new MockMultipartFile(fileName1, fileName1, "png", fileName2.getBytes());
        List<MultipartFile> fileList = List.of(imageFile1, imageFile2);

        UploadProductImagesDto dto = UploadProductImagesDto.builder()
                .productId(productId)
                .imageFiles(fileList)
                .build();

        // mock
        when(productRepositoryQueryMethod.findById(productId))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(ProductNotFoundException.class, () -> productImageService.uploadProductImages(dto));
    }

    /**
     * Helper method creates a mock url of stored file
     *
     * @param key
     * @param multipartFile
     * @return
     */
    private String mockUrl(String key, MultipartFile multipartFile) {
        return "https://storage-service/" + key + multipartFile.getOriginalFilename();
    }

    /**
     * Helper method to mock saving of a list of ProductImage objects
     *
     * @param entities
     * @return
     */
    private List<ProductImage> mockSave(List<ProductImage> entities) {
        return entities.stream().map(this::mockSave).collect(Collectors.toList());
    }

    /**
     * Helper method to mock saving of a ProductImage object
     *
     * @param entities
     * @return
     */
    private ProductImage mockSave(ProductImage entity) {
        entity.setId(UUID.randomUUID().toString());
        entity.setCreatedOn(ZonedDateTime.now());
        entity.setLastModifiedOn(ZonedDateTime.now());
        return entity;
    }
}
