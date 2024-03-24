package com.priyajit.ecommerce.cart.service.service.impl;

import com.priyajit.ecommerce.cart.service.client.FreecurrencyApiClient;
import com.priyajit.ecommerce.cart.service.client.ProductCatalogServiceClient;
import com.priyajit.ecommerce.cart.service.domain.CartProductOperation;
import com.priyajit.ecommerce.cart.service.domain.Currencies;
import com.priyajit.ecommerce.cart.service.dto.CreateCartDto;
import com.priyajit.ecommerce.cart.service.dto.UpdateCartProductQuantityDto;
import com.priyajit.ecommerce.cart.service.exception.*;
import com.priyajit.ecommerce.cart.service.model.CartModel;
import com.priyajit.ecommerce.cart.service.model.CartModelV2;
import com.priyajit.ecommerce.cart.service.mogodoc.Cart;
import com.priyajit.ecommerce.cart.service.mongorepository.CartRepository;
import com.priyajit.ecommerce.cart.service.redisdoc.FreecurrencyApiExchangeRates;
import com.priyajit.ecommerce.cart.service.redisrepository.ExchangeRatesRedisRepository;
import com.priyajit.ecommerce.cart.service.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImplV1 implements CartService {

    private CartRepository cartRepository;
    private ProductCatalogServiceClient productCatalogServiceClient;
    private FreecurrencyApiClient freeCurrencyApiClient;
    private ExchangeRatesRedisRepository exchangeRatesRedisRepository;

    public CartServiceImplV1(CartRepository cartRepository, ProductCatalogServiceClient productCatalogServiceClient, FreecurrencyApiClient freeCurrencyApiClient, ExchangeRatesRedisRepository exchangeRatesRedisRepository) {
        this.cartRepository = cartRepository;
        this.productCatalogServiceClient = productCatalogServiceClient;
        this.freeCurrencyApiClient = freeCurrencyApiClient;
        this.exchangeRatesRedisRepository = exchangeRatesRedisRepository;
    }

    @Override
    public CartModel findCart(String userId) {

        // search in primary DB
        var cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return createCartModel(cart);
    }

    @Override
    public CartModelV2 findCartV2(String userId, String currency) {
        // validate the currency
        if (!Currencies.getCurrencies().contains(currency)) {
            throw new InvalidCurrencyNameException(currency);
        }
        // search in primary DB
        var cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return createCartModelV1(cart, currency);
    }


    @Override
    @Transactional
    public List<CartModel> createCarts(List<CreateCartDto> dtoList) {

        // find Carts which already exists for a userId, do not create duplicate Cart of those userIds
        var userIds = dtoList.stream().map(CreateCartDto::getUserId).collect(Collectors.toList());
        var existingCarts = cartRepository.findAllByUserIdIn(userIds);
        var userIdsWithExistingCarts = existingCarts.stream().map(Cart::getUserId).collect(Collectors.toSet());

        List<Cart> cartList = dtoList.stream()
                .filter(dto -> !userIdsWithExistingCarts.contains(dto.getUserId())) // filter out userIds having existing cart
                .map(this::createCartFromDto)
                .collect(Collectors.toList());

        // save to primary DB
        var savedCarts = cartRepository.saveAll(cartList);

        // create response models & return
        var responseModels = new ArrayList<CartModel>();
        responseModels.addAll(existingCarts.stream().map(this::createCartModel).collect(Collectors.toList()));
        responseModels.addAll(savedCarts.stream().map(this::createCartModel).collect(Collectors.toList()));
        return responseModels;
    }

    @Override
    public CartModel updateCartProductQuantity(UpdateCartProductQuantityDto dto) {
        // validation
        if (dto == null)
            throw new NullArgument("Unexpected null value for arg dto:AddProductRequestDto");

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // update Cart
        doCartOperation(cart, dto.getProductId(), dto.getQuantity(), dto.getOperation());

        // save Cart
        cartRepository.save(cart);

        // create response model and return
        return createCartModel(cart);
    }

    private void doCartOperation(Cart cart, String productId, Long quantity, CartProductOperation operation) {
        // validation
        if (productId == null)
            throw new NullArgument("Unexpected null value for arg productId:String");

        // validation
        if (operation == null)
            throw new NullArgument("Unexpected null value for arg operation:CartProductOperation");

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

    private void doCartOperationIncrease(Cart cart, String productId, Long quantity, Optional<Cart.CartProduct> existingCartProductOpt) {
        // if CartProduct doesn't exist for given productId
        // create CartProduct with given productId and quantity
        if (existingCartProductOpt.isEmpty()) {
            // validate whether a Product exists with given Product
            productCatalogServiceClient.findProductByProductId(productId)
                    .orElseThrow(ProductNotFoundException.supplier(productId));

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

    private void doCartOperationDecrease(Cart cart, String productId, Long quantity, Optional<Cart.CartProduct> existingCartProductOpt) {
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

        Map<String, BigDecimal> exchangeRates = null;
        // search in Redis
        try {
            var exchangeRatesOpt = exchangeRatesRedisRepository.findById(baseCurrency);
            if (exchangeRatesOpt.isPresent()) {
                exchangeRates = exchangeRatesOpt.get().getRates().getData();
            }
        } catch (Exception e) {
            log.error("Error while fetching ExchangeRates object from Redis");
            e.printStackTrace();
        }

        // cache miss, call API
        if (exchangeRates == null) {
            var freeCurrencyApiModel = freeCurrencyApiClient.getExchangeRate(baseCurrency);
            exchangeRates = freeCurrencyApiModel.getData();
            saveExchangeRatesToRedisOptimistic(
                    FreecurrencyApiExchangeRates.builder()
                            .baseCurrency(baseCurrency)
                            .createdOn(ZonedDateTime.now())
                            .rates(freeCurrencyApiModel)
                            .build()
            );
        }

        BigDecimal exchangeRate = exchangeRates.getOrDefault(toCurrency, null);
        if (exchangeRate == null) {
            throw new ExchangeRateNotAvailableException(baseCurrency, toCurrency);
        } else return exchangeRate;
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
            var product = productCatalogServiceClient.findProductByProductId(cartProduct.getProductId())
                    .orElseThrow(ProductNotFoundException.supplier(cartProduct.getProductId()));

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

    private CartModelV2 createCartModelV1(Cart cart, String currency) {
        // create CartProductModel objects & compute cart value
        // cartValue = ∑ (quantity * price)
        BigDecimal cartValue = BigDecimal.ZERO;
        List<CartModelV2.CartProductModel> cartProductModels = new ArrayList<>();
        for (var cartProduct : cart.getProducts()) {

            var product = productCatalogServiceClient.findProductByProductId(cartProduct.getProductId())
                    .orElseThrow(ProductNotFoundException.supplier(cartProduct.getProductId()));

            BigDecimal EXCHANGE_RATE = getExchangeRate(product.getPrice().getCurrencyName(), currency);
            BigDecimal priceInBaseCurrency = EXCHANGE_RATE.multiply(product.getPrice().getPrice());
            priceInBaseCurrency = priceInBaseCurrency.setScale(2, RoundingMode.HALF_EVEN);
            cartValue = cartValue.add(priceInBaseCurrency);

            cartProductModels.add(CartModelV2.CartProductModel.builder()
                    .productId(cartProduct.getProductId())
                    .quantity(cartProduct.getQuantity())
                    .productTitle(product.getTitle())
                    .price(priceInBaseCurrency)
                    .build());
        }

        return CartModelV2.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .products(cartProductModels)
                .cartValue(cartValue)
                .currency(currency)
                .build();
    }

    private Cart createCartFromDto(CreateCartDto dto) {

        return Cart.builder()
                .userId(dto.getUserId())
                .products(new ArrayList<>())
                .build();
    }

    private void saveExchangeRatesToRedisOptimistic(FreecurrencyApiExchangeRates exchangeRates) {
        try {
            CompletableFuture.supplyAsync(() -> exchangeRatesRedisRepository.save(exchangeRates))
                    .thenAccept(saved -> log.info("Saved ExchangeRates object to Redis successfully"))
                    .exceptionally(e -> {
                        log.error("Error occurred while saving ExchangeRates object to Redis, {}", e.getMessage());
                        e.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            log.error("Error occurred while saving ExchangeRates object to Redis, {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
