package com.priyajit.ecommerce.cart.service.controller;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartModelV2;
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
    CartModel findCart(
            @RequestParam(name = "userId") String userId
    ) {
        return cartService.findCart(userId);
    }

    @GetMapping("/v2")
    CartModelV2 findCartV2(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "curreny", defaultValue = "INR") String currency
    ) {
        return cartService.findCartV2(userId, currency);
    }

    @PostMapping
    List<CartModel> createCarts(
            @RequestBody List<CreateCartDto> dtoList
    ) {
        return cartService.createCarts(dtoList);
    }

    @PostMapping("/update-cart-product-quantity")
    CartModel addProduct(
            @RequestBody UpdateCartProductQuantityDto dto
    ) {
        return cartService.updateCartProductQuantity(dto);
    }
}
