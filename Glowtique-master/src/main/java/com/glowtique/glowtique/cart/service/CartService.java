package com.glowtique.glowtique.cart.service;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.repository.CartItemRepository;
import com.glowtique.glowtique.cart.repository.CartRepository;
import com.glowtique.glowtique.exception.CartNotExisting;
import com.glowtique.glowtique.exception.ProductNotfoundException;
import com.glowtique.glowtique.exception.UserNotExisting;
import com.glowtique.glowtique.exception.VoucherAlreadyUsed;
import com.glowtique.glowtique.exception.VoucherNotExistingException;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.user.repository.UserRepository;
import com.glowtique.glowtique.voucher.model.Voucher;
import com.glowtique.glowtique.voucher.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
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
                .totalPrice(BigDecimal.ZERO)
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
        BigDecimal totalPrice = calculateSubtotal(cart);

        if (cart.getUsedVoucher() != null && totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            totalPrice = calculateDiscountedTotal(cart.getUsedVoucher(), totalPrice);
        }

        if (totalPrice.compareTo(BigDecimal.ZERO) == 0) {
            releaseAppliedVoucher(cart);
        }

        cart.setTotalPrice(totalPrice);
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    @Transactional
    public void applyVoucher(UUID userId, String voucherName) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new CartNotExisting("Cart not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExisting("User not found"));
        Voucher voucher = voucherRepository.getVoucherByNameAndUserId(normalizeVoucherCode(voucherName), user.getId())
                .orElseThrow(() -> new VoucherNotExistingException("Няма такъв код за отстъпка!"));

        validateVoucherForCart(voucher, cart);

        BigDecimal subtotal = calculateSubtotal(cart);
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new VoucherNotExistingException("Кошницата е празна.");
        }

        Voucher previousVoucher = cart.getUsedVoucher();
        if (previousVoucher != null && !previousVoucher.getId().equals(voucher.getId())) {
            previousVoucher.setAppliedAt(null);
            previousVoucher.setCart(null);
            voucherRepository.save(previousVoucher);
        }

        cart.setUsedVoucher(voucher);
        voucher.setAppliedAt(LocalDateTime.now());
        voucher.setCart(cart);
        updateTotalPrice(cart);
        cartRepository.save(cart);
        voucherRepository.save(voucher);
    }

    private static void validateVoucherForCart(Voucher voucher, Cart cart) {
        if (voucher.isUsed() || voucher.isTerminated()) {
            throw new VoucherAlreadyUsed("Кодът за отстъпка вече е използван!");
        }

        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new VoucherAlreadyUsed("Кодът за отстъпка е изтекъл!");
        }

        if (cart.getUsedVoucher() != null && cart.getUsedVoucher().getId().equals(voucher.getId())) {
            return;
        }

        if (voucher.getCart() != null && !voucher.getCart().getId().equals(cart.getId())) {
            throw new VoucherAlreadyUsed("Кодът за отстъпка вече е приложен към друга кошница.");
        }

        if (voucher.getOrder() != null && voucher.getOrder().getOrderStatus() == OrderStatus.PENDING) {
            throw new VoucherAlreadyUsed("Кодът за отстъпка вече е приложен към незавършена поръчка.");
        }
    }

    private static @NotNull BigDecimal calculateDiscountedTotal(Voucher voucher, BigDecimal originalPrice) {
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (voucher.getPercentageDiscount() != null) {
            BigDecimal percentage = voucher.getPercentageDiscount();
            discountAmount = originalPrice.multiply(percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        } else if (voucher.getPriceDiscount() != null) {
            discountAmount = voucher.getPriceDiscount();
        }

        BigDecimal newPrice = originalPrice.subtract(discountAmount);
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            newPrice = BigDecimal.ZERO;
        }
        return newPrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void clearAppliedVoucher(User user) {
        Cart cart = user.getCart();
        if (cart == null || cart.getUsedVoucher() == null) {
            return;
        }

        releaseAppliedVoucher(cart);
        updateTotalPrice(cart);
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = user.getCart();
        if (cart == null) {
            return;
        }

        if (cart != null && cart.getCartItems() != null) {
            cartItemRepository.deleteAll(cart.getCartItems());
            cart.getCartItems().clear();
        }
        releaseAppliedVoucher(cart);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private void releaseAppliedVoucher(Cart cart) {
        if (cart == null || cart.getUsedVoucher() == null) {
            return;
        }

        Voucher voucher = cart.getUsedVoucher();
        if (!voucher.isTerminated() && !voucher.isUsed()) {
            voucher.setAppliedAt(null);
            voucher.setCart(null);
            voucherRepository.save(voucher);
        }
        cart.setUsedVoucher(null);
    }

    private String normalizeVoucherCode(String voucherName) {
        if (voucherName == null || voucherName.isBlank()) {
            throw new VoucherNotExistingException("Моля, въведете код за отстъпка.");
        }

        return voucherName.trim().toUpperCase(Locale.ROOT);
    }
}
