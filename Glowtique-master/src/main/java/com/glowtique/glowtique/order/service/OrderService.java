package com.glowtique.glowtique.order.service;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.repository.CartItemRepository;
import com.glowtique.glowtique.cart.repository.CartRepository;
import com.glowtique.glowtique.exception.CartNotExisting;
import com.glowtique.glowtique.exception.NotEnoughProductStock;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderItem;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.order.repository.OrderItemRepository;
import com.glowtique.glowtique.order.repository.OrderRepository;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.web.dto.OrderRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }
    private String customTrackingNumber(String firstName, String lastName) {
        final Random random = new Random();
        final int randomInt = random.nextInt(9999999);

        String firstLetters = (firstName.charAt(0) + "" + lastName.charAt(0)).toUpperCase();

        String sequenceNumber = String.format("%07d", Math.abs(randomInt));

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            char randomChar = (char) ('A' + random.nextInt(26));
            sb.append(randomChar);
        }
        String randomLetters = sb.toString();

        return sequenceNumber + firstLetters + "-" + randomLetters;
    }

    @Transactional
    public Order createOrder(OrderRequest orderRequest, User user, Cart cart) {

        for (CartItem item : cart.getCartItems()) {
            if (item.getQuantity() > item.getProduct().getQuantity()) {
                throw new NotEnoughProductStock("Не е налично количеството от продукта " + item.getProduct().getName() + " !");
            }
        }

        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            return OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getProduct().getPrice())
                    .build();
        }).toList();

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrice.compareTo(BigDecimal.valueOf(200.00)) < 0) {
            totalPrice = totalPrice.add(BigDecimal.valueOf(7.00));
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .orderItems(orderItems)
                .orderMethod(orderRequest.getOrderMethod())
                .totalPrice(totalPrice)
                .trackingNumber(customTrackingNumber(user.getFirstName(), user.getLastName()))
                .postalCode(orderRequest.getPostCode())
                .orderStatus(OrderStatus.PENDING)
                .officeAddress(orderRequest.getOfficeAddress())
                .town(orderRequest.getTown())
                .shippingAddress(orderRequest.getShippingAddress())
                .orderPhoneNumber(orderRequest.getPhoneNumber())
                .payment(null)
                .description(orderRequest.getDescription())
                .build();

        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        orderItemRepository.saveAll(orderItems);
        orderRepository.save(order);

        return order;
    }

    public Order getCurrentOrder(UUID userId) {
        Optional<Order> currentOrder = orderRepository.getLastOrderByUserId(userId);
        if (currentOrder.isEmpty()) {
            throw new CartNotExisting("Кошницата е празна!");
        }
        return currentOrder.get();
    }
    public void completeOrder(User user) {
        Order order = getCurrentOrder(user.getId());
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getQuantity() - item.getQuantity();

            if (newQuantity < 0) {
                throw new NotEnoughProductStock("Няма достатъчно количество от продукта " + item.getProduct().getName() + " !");
            }
            product.setQuantity(newQuantity);
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.ORDER_CONFIRMED);
    }

    public List<Order> allConfirmedOrdersBeforeTwoDays() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        return orderRepository.getAllOrderByOrderStatusAndOrderDateBefore(OrderStatus.ORDER_CONFIRMED, twoDaysAgo);
    }
    public List<Order> allDeliveredOrdersBefore30min() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        return orderRepository.getAllOrderByOrderStatusAndOrderDateBefore(OrderStatus.DELIVERED, thirtyMinutesAgo);
    }

}
