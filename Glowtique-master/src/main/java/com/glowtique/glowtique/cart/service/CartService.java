package com.glowtique.glowtique.cart.service;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.repository.CartItemRepository;
import com.glowtique.glowtique.cart.repository.CartRepository;
import com.glowtique.glowtique.exception.*;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.product.service.ProductService;
import com.glowtique.glowtique.user.repository.UserRepository;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.voucher.model.Voucher;
import com.glowtique.glowtique.voucher.repository.VoucherRepository;
import com.glowtique.glowtique.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository, VoucherRepository voucherRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.voucherRepository = voucherRepository;
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

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotExisting("Cart not found"));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotfoundException("Product not found"));
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExisting("User not found"));

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

    public void applyVoucher(UUID userId, String voucherName) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotExisting("Cart not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExisting("User not found"));
        Voucher voucher = voucherRepository.getVoucherByNameAndUserId(voucherName, user.getId()).orElseThrow(() -> new VoucherNotExistingException("Няма такъв код за отсъпка !"));

        BigDecimal newPrice = getBigDecimal(voucher, cart);

        cart.setTotalPrice(newPrice);
        cart.setUsedVoucher(voucher);
        cartRepository.save(cart);

        voucher.setAppliedAt(LocalDateTime.now());
        voucher.setUsed(true);
        voucherRepository.save(voucher);
    }

    private static @NotNull BigDecimal getBigDecimal(Voucher voucher, Cart cart) {
        if (voucher.isUsed() || voucher.equals(cart.getUsedVoucher())) {
            throw new VoucherAlreadyUsed("Кодът за отсъпка вече е използван !");
        }

        BigDecimal originalPrice = cart.getTotalPrice();
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (voucher.getPercentageDiscount() != null) {
            BigDecimal percentage = voucher.getPercentageDiscount();
            discountAmount = originalPrice.multiply(percentage.divide(BigDecimal.valueOf(100)));
        } else if (voucher.getPriceDiscount() != null) {
            discountAmount = voucher.getPriceDiscount();
        }

        BigDecimal newPrice = originalPrice.subtract(discountAmount);
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            newPrice = BigDecimal.ZERO;
        }
        return newPrice;
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
