package com.priyajit.ecommerce.cart.service.service;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartModelV2;

public interface CartService {

    CartModel findCart(String userId);

    CartModelV2 findCartV2(String userId, String currency);

    CartModel createCart(CreateCartDto dto);

    CartModel updateCartProductQuantity(UpdateCartProductQuantityDto dto);
}
