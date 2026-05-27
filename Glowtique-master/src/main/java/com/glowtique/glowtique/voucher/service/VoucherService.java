package com.glowtique.glowtique.voucher.service;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.exception.VoucherNotExistingException;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.voucher.model.Voucher;
import com.glowtique.glowtique.voucher.model.VoucherType;
import com.glowtique.glowtique.voucher.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class VoucherService {
    private static final int VOUCHER_VALID_DAYS = 30;

    private final CartService cartService;
    private final VoucherRepository voucherRepository;
    private final Random random = new Random();

    @Autowired
    public VoucherService(CartService cartService, VoucherRepository voucherRepository) {
        this.cartService = cartService;
        this.voucherRepository = voucherRepository;
    }

    public Voucher getVoucher(String name) {
        return voucherRepository.getVoucherByName(normalizeVoucherName(name))
                .orElseThrow(() -> new VoucherNotExistingException("Не съществува такъв код за отстъпка!"));
    }

    public Voucher getVoucherByNameAndUserId(String voucherName, UUID userId) {
        return voucherRepository.getVoucherByNameAndUserId(normalizeVoucherName(voucherName), userId)
                .orElseThrow(() -> new VoucherNotExistingException("Не съществува такъв код за отстъпка!"));
    }

    public List<Voucher> getAllVouchersByUserId(UUID userId) {
        return voucherRepository.findVouchersByUserId(userId);
    }

    public Voucher createPercentageVoucher(User user, BigDecimal percent, VoucherType voucherType) {
        if (percent == null || percent.compareTo(BigDecimal.ZERO) <= 0 || percent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage voucher discount must be between 0 and 100.");
        }

        Voucher voucher = Voucher.builder()
                .user(user)
                .name(createVoucherName())
                .voucherType(voucherType)
                .priceDiscount(null)
                .percentageDiscount(percent)
                .isUsed(false)
                .isTerminated(false)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(VOUCHER_VALID_DAYS))
                .appliedAt(null)
                .cart(null)
                .build();

        return voucherRepository.save(voucher);
    }

    public Voucher createPriceVoucher(User user, BigDecimal price, VoucherType voucherType) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price voucher discount must be positive.");
        }

        Voucher voucher = Voucher.builder()
                .user(user)
                .name(createVoucherName())
                .voucherType(voucherType)
                .priceDiscount(price)
                .percentageDiscount(null)
                .isUsed(false)
                .isTerminated(false)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(VOUCHER_VALID_DAYS))
                .appliedAt(null)
                .cart(null)
                .build();

        return voucherRepository.save(voucher);
    }

    public Voucher getLastUsedVoucher(User user) {
        return voucherRepository.getVoucherByUserAndUsedAfterAt(user)
                .orElseThrow(() -> new VoucherNotExistingException("Такъв ваучер не е намерен!"));
    }

    private String createVoucherName() {
        List<String> voucherNames = List.of("GLOWISH", "CHARME", "SWEETON", "MOONLIT", "SPARKLY", "LOVENA", "POPPY", "CHICLY", "SOFTLY", "BRILLA");

        String voucherName;
        do {
            voucherName = voucherNames.get(random.nextInt(voucherNames.size())) + "-" + String.format("%04d", random.nextInt(10000));
        } while (voucherRepository.getVoucherByName(voucherName).isPresent());

        return voucherName;
    }

    @Transactional
    public void terminateVoucher(Voucher voucher, Cart cart) {
        if (voucher == null) {
            return;
        }

        voucher.setUsed(true);
        voucher.setTerminated(true);
        voucher.setAppliedAt(LocalDateTime.now());
        voucher.setCart(null);

        if (cart != null && voucher.equals(cart.getUsedVoucher())) {
            cart.setUsedVoucher(null);
        }

        voucherRepository.save(voucher);
    }

    @Transactional
    public void completeVoucherUse(Voucher voucher, Cart cart, Order order) {
        if (voucher == null) {
            return;
        }

        voucher.setOrder(order);
        terminateVoucher(voucher, cart);
    }

    @Transactional
    public void releasePendingVoucher(Voucher voucher) {
        if (voucher == null || voucher.isUsed() || voucher.isTerminated()) {
            return;
        }

        voucher.setAppliedAt(null);
        voucher.setCart(null);
        voucher.setOrder(null);
        voucherRepository.save(voucher);
    }

    public boolean isEqualToCompletedOrders(User user) {
        long activeVoucherCount = Objects.requireNonNullElse(user.getVouchers(), java.util.Set.<Voucher>of()).stream()
                .filter(voucher -> voucher.getExpiryDate() == null || voucher.getExpiryDate().isAfter(LocalDateTime.now()))
                .count();
        long completedOrderCount = Objects.requireNonNullElse(user.getOrders(), java.util.Set.<Order>of()).stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.COMPLETED || order.getOrderStatus() == OrderStatus.ORDER_CONFIRMED)
                .count();

        return activeVoucherCount <= completedOrderCount;
    }

    private String normalizeVoucherName(String voucherName) {
        if (voucherName == null || voucherName.isBlank()) {
            throw new VoucherNotExistingException("Моля, въведете код за отстъпка.");
        }

        return voucherName.trim().toUpperCase(Locale.ROOT);
    }
}
