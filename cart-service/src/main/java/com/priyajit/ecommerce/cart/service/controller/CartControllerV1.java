package com.priyajit.ecommerce.cart.service.controller;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartModelV2;
import com.priyajit.ecommerce.cart.service.model.Response;
import com.priyajit.ecommerce.cart.service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        try {
            var model = cartService.findCart(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<CartModel>builder().data(model).build());
        } catch (ResponseStatusException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<CartModel>builder().error(e.getReason()).build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<CartModel>builder().build());
        }
    }

    @GetMapping("/v2")
    public ResponseEntity<Response<CartModelV2>> findCartV2(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "curreny", defaultValue = "INR") String currency
    ) {
        try {
            var model = cartService.findCartV2(userId, currency);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<CartModelV2>builder().data(model).build());
        } catch (ResponseStatusException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<CartModelV2>builder().error(e.getReason()).build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<CartModelV2>builder().build());
        }
    }

    @PostMapping
    public ResponseEntity<Response<List<CartModel>>> createCarts(
            @RequestBody List<CreateCartDto> dtoList
    ) {
        try {
            var model = cartService.createCarts(dtoList);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<List<CartModel>>builder().data(model).build());
        } catch (ResponseStatusException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<List<CartModel>>builder().error(e.getReason()).build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<List<CartModel>>builder().build());
        }
    }

    @PostMapping("/update-cart-product-quantity")
    public ResponseEntity<Response<CartModel>> addProduct(
            @RequestBody UpdateCartProductQuantityDto dto
    ) {
        try {
            var model = cartService.updateCartProductQuantity(dto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Response.<CartModel>builder().data(model).build());
        } catch (ResponseStatusException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getStatusCode())
                    .body(Response.<CartModel>builder().error(e.getReason()).build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<CartModel>builder().build());
        }
    }
}
