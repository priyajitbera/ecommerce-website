package com.priyajit.ecommerce.cart.service.service.impl;

import com.priyajit.ecommerce.cart.service.component.ExchangeRatesRepository;
import com.priyajit.ecommerce.cart.service.domain.CartProductOperation;
import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.exception.BadProductQuantityValueException;
import com.priyajit.ecommerce.cart.service.exception.ExchangeRateNotAvailableException;
import com.priyajit.ecommerce.cart.service.exception.InvalidCurrencyNameException;
import com.priyajit.ecommerce.cart.service.exception.NullArgument;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartWithValueModel;
import com.priyajit.ecommerce.cart.service.mogodoc.Cart;
import com.priyajit.ecommerce.cart.service.mongorepository.CartRepository;
import com.priyajit.ecommerce.cart.service.service.CartService;
import com.priyajit.ecommerce.product.catalog.service.api.CurrencyControllerV1Api;
import com.priyajit.ecommerce.product.catalog.service.api.ProductControllerV1Api;
import com.priyajit.ecommerce.product.catalog.service.model.CurrencyModel;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImplV1 implements CartService {

    private CartRepository cartRepository;
    private ProductControllerV1Api productControllerV1Api;
    private CurrencyControllerV1Api currencyControllerV1Api;
    private ExchangeRatesRepository exchangeRatesRepository;

    public CartServiceImplV1(
            CartRepository cartRepository,
            ProductControllerV1Api productControllerV1Api,
            CurrencyControllerV1Api currencyControllerV1Api,
            ExchangeRatesRepository exchangeRatesRepository
    ) {
        this.cartRepository = cartRepository;
        this.productControllerV1Api = productControllerV1Api;
        this.currencyControllerV1Api = currencyControllerV1Api;
        this.exchangeRatesRepository = exchangeRatesRepository;
    }

    @Override
    public CartModel findCart(String userId) {

        // search in primary DB
        var cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return createCartModel(cart);
    }

    @Override
    public CartWithValueModel findCartWithValue(String userId, String currency) {
        // validate the currency
        // get all currencies
        var allCurrencies = currencyControllerV1Api.findCurrencies(null, null)
                .stream().map(CurrencyModel::getId).collect(Collectors.toList());

        if (!allCurrencies.contains(currency))
            throw new InvalidCurrencyNameException(currency, allCurrencies);

        // search in primary DB
        var cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return createCartWithValueModel(cart, currency);
    }


    @Override
    @Transactional
    public CartModel createCart(@Valid CreateCartDto dto) {
        if (dto == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unexpected null value for argument dto:CreateCartDto");

        // find Carts which already exists for a userId, do not create duplicate Cart of those userIds
        var cartOpt = cartRepository.findByUserId(dto.getUserId());
        if (cartOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart already exists for user with userId:" + dto.getUserId());
        }

        // create Cart object from dto
        var cart = createCartFromDto(dto);

        // save to primary DB
        var savedCart = cartRepository.save(cart);

        // create response models & return
        return createCartModel(savedCart);
    }

    @Override
    public CartModel updateCartProductQuantity(String userId, @Valid UpdateCartProductQuantityDto dto) {
        // validation
        if (userId == null)
            throw new NullArgument("Unexpected null value for arg userId:String");
        if (dto == null)
            throw new NullArgument("Unexpected null value for arg dto:AddProductRequestDto");

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // validation: given userId is same as cart's userId
        if (!userId.equals(cart.getUserId())) {
            new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // update Cart
        doCartOperation(cart, dto.getProductId(), dto.getQuantity(), dto.getOperation());

        // save Cart
        cartRepository.save(cart);

        // create response model and return
        return createCartModel(cart);
    }

    private void doCartOperation(Cart cart, String productId, Long quantity, CartProductOperation operation) {

        // initialize empty list if not already
        if (cart.getProducts() == null) {
            cart.setProducts(new ArrayList<>());
        }

        // check if CartProduct is already added for the given ProductId
        // filter CartProducts by given ProductId, expected either 0 or 1 CartProduct for a productId
        List<Cart.CartProduct> filteredCartProducts = cart.getProducts().stream()
                .filter(cartProduct -> Objects.equals(cartProduct.getProductId(), productId))
                .collect(Collectors.toList());

        Optional<Cart.CartProduct> existingCartProductOpt = Optional.empty();
        if (filteredCartProducts.size() > 0) {
            existingCartProductOpt = Optional.of(filteredCartProducts.get(0));
        }

        // INCREMENT product quantity in Cart by given quantity
        if (CartProductOperation.INCREASE == operation) {
            doCartOperationIncrease(cart, productId, quantity, existingCartProductOpt);

        }
        // DECREASE product quantity in Cart by given quantity
        else if (CartProductOperation.DECREASE == operation) {
            doCartOperationDecrease(cart, productId, quantity, existingCartProductOpt);

        }
        // REMOVE product from Cart
        else if (CartProductOperation.REMOVE == operation) {
            doCartOperationRemove(cart, existingCartProductOpt);
        }
    }

    private void doCartOperationIncrease(Cart cart, String productId, Long
            quantity, Optional<Cart.CartProduct> existingCartProductOpt) {
        // if CartProduct doesn't exist for given productId
        // create CartProduct with given productId and quantity
        if (existingCartProductOpt.isEmpty()) {
            // validate whether a Product exists with given Product
            productControllerV1Api.findOneById(productId);

            // validate quantity
            if (quantity <= 0) {
                throw new BadProductQuantityValueException("Product quantity to add cannot be less than or equal to zero");
            }

            // create Product & CartProduct object from productModel
            Cart.CartProduct cartProduct = Cart.CartProduct.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            cart.getProducts().add(cartProduct);
        }
        // if CartProduct already exists for the productId, only update quantity
        else {
            long existingQuantity = existingCartProductOpt.get().getQuantity() == null ? 0 : existingCartProductOpt.get().getQuantity();
            long newQuantity = existingQuantity + quantity;
            existingCartProductOpt.get().setQuantity(newQuantity);
        }
    }

    private void doCartOperationDecrease(Cart cart, String productId, Long
            quantity, Optional<Cart.CartProduct> existingCartProductOpt) {
        // since SUBTRACT operation only update quantity if CartProduct exist
        if (existingCartProductOpt.isPresent()) {
            // validate quantity
            if (quantity <= 0) {
                throw new BadProductQuantityValueException("Product quantity to subtract cannot be less than or equal to zero");
            }

            long existingQuantity = existingCartProductOpt.get().getQuantity() == null ? 0 : existingCartProductOpt.get().getQuantity();
            long newQuantity = existingQuantity - quantity;
            if (newQuantity < 0) {
                throw new BadProductQuantityValueException("Product quantity to remove cannot be more than the existing quantity on Cart");
            }
            // if new quantity is 0 i.e. equivalent of REMOVE operation
            // remove the CartProduct object
            else if (newQuantity == 0) {
                cart.getProducts().remove(existingCartProductOpt.get());
            } else {
                existingCartProductOpt.get().setQuantity(newQuantity);
            }
        }
    }

    private BigDecimal getExchangeRate(String baseCurrency, String toCurrency) {
        if (baseCurrency.equals(toCurrency)) return BigDecimal.ONE;
        return exchangeRatesRepository.getExchangeRate(baseCurrency, toCurrency)
                .orElseThrow(ExchangeRateNotAvailableException.supplier(baseCurrency, toCurrency));
    }

    private void doCartOperationRemove(Cart cart, Optional<Cart.CartProduct> existingCartProductOpt) {
        // since REMOVE operation only remove CartProduct object if exists
        if (existingCartProductOpt.isPresent()) {
            cart.getProducts().remove(existingCartProductOpt.get());
        }
    }

    private CartModel createCartModel(Cart cart) {

        // create CartProductModel objects
        var cartProducts = cart.getProducts().stream().map(cartProduct -> {
            var product = productControllerV1Api.findOneById(cartProduct.getProductId());

            return CartModel.CartProductModel.builder()
                    .productId(cartProduct.getProductId())
                    .quantity(cartProduct.getQuantity())
                    .productTitle(product.getTitle())
                    .build();

        }).collect(Collectors.toList());

        // create CartModel object and return
        return CartModel.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .products(cartProducts)
                .build();
    }

    private CartWithValueModel createCartWithValueModel(Cart cart, String currency) {
        // create CartProductModel objects & compute cart value
        // cartValue = ∑ (quantity * price)
        BigDecimal cartValue = BigDecimal.ZERO;
        List<CartWithValueModel.CartProductModel> cartProductModels = new ArrayList<>();
        for (var cartProduct : cart.getProducts()) {

            var product = productControllerV1Api.findOneById(cartProduct.getProductId());
            BigDecimal EXCHANGE_RATE = getExchangeRate(product.getPrice().getCurrency().getId(), currency);
            log.info("[createCartWithValueModel] baseCurrency: {} toCurrency: {} EXCHANGE_RATE: {}", product.getPrice().getCurrency().getId(), currency, EXCHANGE_RATE);
            BigDecimal priceInBaseCurrency = EXCHANGE_RATE
                    .multiply(product.getPrice().getPrice())
                    .multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
            priceInBaseCurrency = priceInBaseCurrency.setScale(2, RoundingMode.HALF_EVEN);
            cartValue = cartValue.add(priceInBaseCurrency);

            cartProductModels.add(CartWithValueModel.CartProductModel.builder()
                    .productId(cartProduct.getProductId())
                    .quantity(cartProduct.getQuantity())
                    .productTitle(product.getTitle())
                    .price(priceInBaseCurrency)
                    .build());
        }

        return CartWithValueModel.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .products(cartProductModels)
                .cartValue(cartValue)
                .currency(currency)
                .build();
    }

    /**
     * Helper method creates Cart object from CreateCartDto object
     *
     * @param dto
     * @return
     */
    private Cart createCartFromDto(CreateCartDto dto) {

        return Cart.builder()
                .userId(dto.getUserId())
                .products(new ArrayList<>())
                .build();
    }
}
