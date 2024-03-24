package com.priyajit.ecommerce.cart.service.service;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartModelV2;

import java.util.List;

public interface CartService {

    CartModel findCart(String userId);

    CartModelV2 findCartV2(String userId, String currency);

    List<CartModel> createCarts(List<CreateCartDto> dtoList);

    CartModel updateCartProductQuantity(UpdateCartProductQuantityDto dto);
}
