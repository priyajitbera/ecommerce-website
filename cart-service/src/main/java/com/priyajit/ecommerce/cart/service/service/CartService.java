package com.priyajit.ecommerce.cart.service.service;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartWithValueModel;

public interface CartService {

    CartModel findCart(String userId);

    CartWithValueModel findCartWithValue(String userId, String currency);

    CartModel createCart(CreateCartDto dto);

    CartModel updateCartProductQuantity(String userId, UpdateCartProductQuantityDto dto);
}
