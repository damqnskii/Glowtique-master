package com.glowtique.glowtique.payment;

import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.payment.model.PaymentMethod;
import com.glowtique.glowtique.payment.model.PaymentStatus;
import com.glowtique.glowtique.payment.service.PaymentService;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.user.repository.UserRepository;
import com.glowtique.glowtique.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUTest {
    @Mock
    private UserService userService;
    @Mock
    private OrderService orderService;
    @Mock
    private CartService cartService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PaymentService paymentService;
    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setLoyaltyPoints(10);

        order = new Order();
        order.setTotalPrice(BigDecimal.valueOf(100));
        order.setOrderItems(new ArrayList<>());

    }


    @Test
    void correctlyPaymentProcessing() {
        when(orderService.getCurrentOrder(user.getId())).thenReturn(order);

        String result = paymentService.processPayment(PaymentMethod.CASH, null, null, null, null, user);

        assertEquals("order-confirmation", result);
        assertNotNull(order.getPayment());
        assertEquals(PaymentStatus.COMPLETED, order.getPayment().getStatus());
        assertEquals(PaymentMethod.CASH, order.getPayment().getPaymentMethod());

        verify(orderService, times(3)).getCurrentOrder(user.getId());
        verify(userRepository).save(user);
        verify(orderService).completeOrder(user);
        verify(cartService).clearCart(user);

    }

    @Test
    void testLoyaltyPointsUpdate() {
        when(orderService.getCurrentOrder(user.getId())).thenReturn(order);

        paymentService.processPayment(PaymentMethod.CASH, null, null, null, null, user);

        int expectedPoints = 10 + 100 / 10;
        assertEquals(expectedPoints, user.getLoyaltyPoints());

        verify(orderService, times(3)).getCurrentOrder(user.getId());
    }
    @Test
    void gettingUnsupportedPaymentException() {
        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(PaymentMethod.EASY_PAY, null, null, null, null, user));
    }


}

