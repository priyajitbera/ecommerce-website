package com.priyajit.ecommerce.cart.service.controller;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartWithValueModel;
import com.priyajit.ecommerce.cart.service.service.CartService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/v1/cart")
@CrossOrigin("*")
public class CartControllerV1 {

    private CartService cartService;

    public CartControllerV1(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartModel> findCart(
            @RequestParam(name = "userId") String userId
    ) {
        return ok(cartService.findCart(userId));
    }

    @GetMapping("/with-value")
    public ResponseEntity<CartWithValueModel> findCartWithValue(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "currency", defaultValue = "INR") String currency
    ) {
        return ok(cartService.findCartWithValue(userId, currency));
    }

    @PostMapping
    public ResponseEntity<CartModel> createCart(
            @Valid @RequestBody CreateCartDto dtoList
    ) {
        return ok(cartService.createCart(dtoList));
    }

    @PostMapping("/update-cart-product-quantity")
    public ResponseEntity<CartModel> updateCartProductQuantity(
            @RequestHeader(name = "userId") String userId,
            @Valid @RequestBody UpdateCartProductQuantityDto dto
    ) {
        return ok(cartService.updateCartProductQuantity(userId, dto));
    }
}
