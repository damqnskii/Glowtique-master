package com.glowtique.glowtique.payment.service;

import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.payment.model.Payment;
import com.glowtique.glowtique.payment.model.PaymentMethod;
import com.glowtique.glowtique.payment.model.PaymentStatus;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.repository.UserRepository;
import com.glowtique.glowtique.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    private final UserService userService;
    private final OrderService orderService;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public PaymentService(UserService userService, OrderService orderService, CartService cartService, UserRepository userRepository, ProductRepository productRepository) {
        this.userService = userService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }
    @Transactional
    public String processPayment(PaymentMethod paymentMethod, String cardNumber,
                                 String expiryDate, String cvc, String giftCardNumber, User user) {
        switch (paymentMethod) {
            case CASH:
                return cashProcessPayment(user);
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

    }

    private String cashProcessPayment(User user) {
         orderService.getCurrentOrder(user.getId()).setPayment(Payment.builder()
                        .order(orderService.getCurrentOrder(user.getId()))
                        .status(PaymentStatus.COMPLETED)
                        .transactionId(UUID.randomUUID().toString())
                        .createdAt(LocalDateTime.now())
                        .paymentMethod(PaymentMethod.CASH)
                .build());

        loyaltyPointsUpdate(user);


        userRepository.save(user);
        orderService.completeOrder(user);
        cartService.clearCart(user);
        return "order-confirmation";
    }

    private void loyaltyPointsUpdate(User user) {
        BigDecimal totalPrice = orderService.getCurrentOrder(user.getId()).getTotalPrice();
        int currentLoyaltyPoints = user.getLoyaltyPoints();
        BigDecimal obtainedAmount = totalPrice.divide(BigDecimal.valueOf(10.0)).round(new MathContext(0, RoundingMode.CEILING));
        user.setLoyaltyPoints(obtainedAmount.intValue() + currentLoyaltyPoints);
    }

}
