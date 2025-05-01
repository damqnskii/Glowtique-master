package com.glowtique.glowtique.cart.service;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.repository.CartItemRepository;
import com.glowtique.glowtique.cart.repository.CartRepository;
import com.glowtique.glowtique.exception.CartNotExisting;
import com.glowtique.glowtique.exception.ProductNotfoundException;
import com.glowtique.glowtique.exception.UserNotExisting;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart createCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .totalPrice(BigDecimal.valueOf(0))
                .build();

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addItemToCart(UUID userId, UUID productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExisting("User with " + userId + " not found"));

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotExisting("Cart not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotfoundException("Product with " + productId + " not found"));
        CartItem existingCartItem = getCartItem(cart, product);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();

            cart.getCartItems().add(cartItem);
        }

        updateTotalPrice(cart);

        return cartRepository.save(cart);
    }
    public CartItem getCartItem(Cart cart, Product product) {
        return cart.getCartItems()
                .stream()
                .filter(i -> i.getProduct().getId()
                        .equals(product.getId())).findFirst().orElse(null);
    }

    public Cart getCartByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExisting("User with ID " + userId + " not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));
    }
    private void updateTotalPrice(Cart cart) {
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalPrice);
    }
    @Transactional
    public Cart removeItemFromCart(UUID userId, UUID productId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotExisting("Cart not found"));

        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotfoundException("Product not found in cart"));

        cart.getCartItems().remove(cartItemToRemove);

        cartItemRepository.delete(cartItemToRemove);

        updateTotalPrice(cart);
        return cartRepository.save(cart);
    }
    @Transactional
    public Cart updateQuantity(UUID userId, UUID productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotExisting("Cart not found"));
        CartItem cartItem = cart.getCartItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new ProductNotfoundException("Product not found in the cart"));
        if (quantity > 0) {
            cartItem.setQuantity(quantity);
        } else {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        }
        updateTotalPrice(cart);
        return cartRepository.save(cart);
    }
    public void clearCart(User user) {
        Cart cart = user.getCart();
        if (cart != null && cart.getCartItems() != null) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
        }
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}
