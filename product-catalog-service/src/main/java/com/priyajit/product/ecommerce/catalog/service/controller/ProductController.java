package com.priyajit.product.ecommerce.catalog.service.controller;

import com.priyajit.product.ecommerce.catalog.service.dto.CreateProductDto;
import com.priyajit.product.ecommerce.catalog.service.dto.UpdateProductDto;
import com.priyajit.product.ecommerce.catalog.service.entity.Product;
import com.priyajit.product.ecommerce.catalog.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    List<Product> findProducts(
            @RequestParam(required = false) List<Long> productIds,
            @RequestParam(name = "pageIndex", required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        return productService.findProducts(productIds, pageIndex, pageSize);
    }

    @GetMapping("/categories")
    List<String> findProductCategories() {
        return productService.findProductCategories();
    }

    @GetMapping("/category/{category}")
    List<Product> findProductsByCategories(
            @PathVariable(name = "category") String category
    ) {
        return productService.findProductByCategories(category);
    }

    @PostMapping
    List<Product> createProducts(
            @RequestBody List<CreateProductDto> dtoList
    ) {
        return productService.createProducts(dtoList);
    }

    @PatchMapping("/{productId}")
    Product updateProduct(
            @PathVariable(name = "productId") Long productId,
            @RequestBody UpdateProductDto dto
    ) {
        return productService.updateProduct(productId, dto);
    }

    @DeleteMapping("/{productId}")
    Product deleteProduct(
            @PathVariable(name = "productId") Long productId
    ) {
        return productService.deleteProduct(productId);
    }
}
