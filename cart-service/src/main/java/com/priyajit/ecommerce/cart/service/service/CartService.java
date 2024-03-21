package com.priyajit.ecommerce.cart.service.service;

import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.model.CartModel;

import java.util.List;

public interface CartService {

    CartModel findCart(String userId);

    List<CartModel> createCarts(List<CreateCartDto> dtoList);

    CartModel updateCartProductQuantity(UpdateCartProductQuantityDto dto);
}
