package com.priyajit.ecommerce.cart.service.controller;

import com.priyajit.ecommerce.cart.service.dto.AddProductRequestDto;
import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.entity.Cart;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cart")
public class CartControllerV1 {

    private CartService cartService;

    public CartControllerV1(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    CartModel findCarts(
            @RequestParam(name = "userId") Long userId
    ) {
        return cartService.findCart(userId);
    }

    @PostMapping
    List<CartModel> createCarts(
            @RequestBody List<CreateCartDto> dtoList
    ) {
        return cartService.createCarts(dtoList);
    }

    @PostMapping("/add-product")
    CartModel addProduct(
            @RequestBody AddProductRequestDto dto
    ) {
        return cartService.addProduct(dto);
    }
}
