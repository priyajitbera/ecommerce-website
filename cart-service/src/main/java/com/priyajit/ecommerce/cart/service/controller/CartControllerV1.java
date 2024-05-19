package com.priyajit.ecommerce.cart.service.controller;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartModelV2;
import com.priyajit.ecommerce.cart.service.model.Response;
import com.priyajit.ecommerce.cart.service.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.priyajit.ecommerce.cart.service.controller.ControllerHelper.supplyResponse;

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
    public ResponseEntity<Response<CartModel>> findCart(
            @RequestParam(name = "userId") String userId
    ) {
        return supplyResponse(() -> cartService.findCart(userId), log);
    }

    @GetMapping("/v2")
    public ResponseEntity<Response<CartModelV2>> findCartV2(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "curreny", defaultValue = "INR") String currency
    ) {
        return supplyResponse(() -> cartService.findCartV2(userId, currency), log);
    }

    @PostMapping
    public ResponseEntity<Response<List<CartModel>>> createCarts(
            @RequestBody List<CreateCartDto> dtoList
    ) {
        return supplyResponse(() -> cartService.createCarts(dtoList), log);
    }

    @PostMapping("/update-cart-product-quantity")
    public ResponseEntity<Response<CartModel>> addProduct(
            @RequestBody UpdateCartProductQuantityDto dto
    ) {
        return supplyResponse(() -> cartService.updateCartProductQuantity(dto), log);
    }
}
