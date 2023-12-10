package com.priyajit.ecommerce.cart.service.service.impl;

import com.priyajit.ecommerce.cart.service.dto.AddProductRequestDto;
import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.entity.Cart;
import com.priyajit.ecommerce.cart.service.entity.CartProduct;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartProductModel;
import com.priyajit.ecommerce.cart.service.repository.CartRepository;
import com.priyajit.ecommerce.cart.service.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImplV1 implements CartService {

    private CartRepository cartRepository;

    public CartServiceImplV1(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public CartModel findCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return createCartModel(cart);
    }

    @Override
    public List<CartModel> createCarts(List<CreateCartDto> dtoList) {

        List<Cart> cartList = dtoList.stream()
                .map(this::createCartFromDto)
                .collect(Collectors.toList());

        return cartRepository.saveAllAndFlush(cartList).stream()
                .map(this::createCartModel)
                .collect(Collectors.toList());
    }

    @Override
    public CartModel addProduct(AddProductRequestDto dto) {

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<CartProduct> cartProducts = cart.getProducts().stream()
                .filter(cartProduct -> cartProduct.getProductId().equals(dto.getProductId()))
                .collect(Collectors.toList());

        if (cartProducts.size() < 1) {
            CartProduct cartProduct = CartProduct.builder()
                    .productId(dto.getProductId())
                    .quantity(dto.getQuantity())
                    .cart(cart)
                    .build();
            cart.getProducts().add(cartProduct);
        } else {
            cartProducts.get(0).setQuantity(cartProducts.get(0).getQuantity() + dto.getQuantity());
        }

        cartRepository.saveAndFlush(cart);

        return createCartModel(cart);
    }

    private CartModel createCartModel(Cart cart) {

        return CartModel.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .products(cart.getProducts().stream().map(this::createCartProductModel).collect(Collectors.toList()))
                .build();
    }

    private CartProductModel createCartProductModel(CartProduct cartProduct) {

        return CartProductModel.builder()
                .productId(cartProduct.getProductId())
                .quantity(cartProduct.getQuantity())
                .build();
    }

    private Cart createCartFromDto(CreateCartDto dto) {

        return Cart.builder()
                .userId(dto.getUserId())
                .products(new ArrayList<>())
                .build();
    }
}
