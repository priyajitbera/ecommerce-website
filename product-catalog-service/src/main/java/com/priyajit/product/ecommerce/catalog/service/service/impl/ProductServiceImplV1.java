package com.priyajit.product.ecommerce.catalog.service.service.impl;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.UpdateProductDto;
import com.priyajit.product.ecommerce.catalog.service.entity.Product;
import com.priyajit.product.ecommerce.catalog.service.repository.ProductRepository;
import com.priyajit.product.ecommerce.catalog.service.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplV1 implements ProductService {

    private ProductRepository productRepository;

    public ProductServiceImplV1(ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findProducts(List<Long> productIds, int pageIndex, int pageSize) {

        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize);
        if (productIds == null) {
            return productRepository.findAll(pageRequest).stream().toList();
        } else {
            return productRepository.findByIdIn(productIds, pageRequest).stream().toList();
        }
    }

    @Override
    public List<String> findProductCategories() {

        return productRepository.findProductCategories();
    }

    @Override
    public List<Product> findProductByCategories(String category) {

        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> createProducts(List<CreateProductDto> dtoList) {

        List<Product> productList = dtoList.stream()
                .map(this::createProductFromDto)
                .collect(Collectors.toList());

        return productRepository.saveAllAndFlush(productList);
    }

    private Product createProductFromDto(CreateProductDto dto) {

        return Product.builder()
                .title(dto.getTitle())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .image(dto.getImage())
                .build();
    }

    @Override
    public Product updateProduct(Long productId, UpdateProductDto dto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        updateProductFromDto(product, dto);
        return productRepository.saveAndFlush(product);
    }

    private void updateProductFromDto(Product product, UpdateProductDto dto) {

        if (dto.getTitle() != null)
            product.setTitle(dto.getTitle());
        if (dto.getPrice() != null)
            product.setPrice(dto.getPrice());
        if (dto.getDescription() != null)
            product.setDescription(dto.getDescription());
        if (dto.getCategory() != null)
            product.setCategory(dto.getCategory());
        if (dto.getImage() != null)
            product.setImage(dto.getImage());
    }

    @Override
    public Product deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        productRepository.delete(product);
        return product;
    }
}
