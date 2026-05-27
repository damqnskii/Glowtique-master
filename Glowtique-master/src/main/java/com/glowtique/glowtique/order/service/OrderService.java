package com.glowtique.glowtique.order.service;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.repository.CartItemRepository;
import com.glowtique.glowtique.cart.repository.CartRepository;
import com.glowtique.glowtique.exception.CartNotExisting;
import com.glowtique.glowtique.exception.NotEnoughProductStock;
import com.glowtique.glowtique.exception.VoucherAlreadyUsed;
import com.glowtique.glowtique.order.event.OrderCompletedEvent;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderItem;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.order.repository.OrderItemRepository;
import com.glowtique.glowtique.order.repository.OrderRepository;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.voucher.model.Voucher;
import com.glowtique.glowtique.voucher.service.VoucherService;
import com.glowtique.glowtique.web.dto.OrderRequest;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final VoucherService voucherService;

    public OrderService(ApplicationEventPublisher eventPublisher, CartRepository cartRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, VoucherService voucherService) {
        this.eventPublisher = eventPublisher;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.voucherService = voucherService;
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

        Voucher voucher = cart.getUsedVoucher();
        if (voucher != null) {
            totalPrice = applyVoucherDiscount(totalPrice, voucher);
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
                .voucher(voucher)
                .description(orderRequest.getDescription())
                .build();

        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        if (voucher != null) {
            voucher.setOrder(order);
            voucher.setCart(null);
            voucher.setAppliedAt(null);
            cart.setUsedVoucher(null);
            cart.setTotalPrice(orderItems.stream()
                    .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        orderItemRepository.saveAll(orderItems);
        orderRepository.save(order);
        cartRepository.save(cart);

        return order;
    }

    private BigDecimal applyVoucherDiscount(BigDecimal totalPrice, Voucher voucher) {
        if (voucher.isUsed() || voucher.isTerminated()) {
            throw new VoucherAlreadyUsed("Кодът за отстъпка вече е използван!");
        }

        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new VoucherAlreadyUsed("Кодът за отстъпка е изтекъл!");
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (voucher.getPriceDiscount() != null) {
            discount = voucher.getPriceDiscount();
        } else if (voucher.getPercentageDiscount() != null) {
            discount = totalPrice.multiply(voucher.getPercentageDiscount().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        }

        BigDecimal discountedTotal = totalPrice.subtract(discount);
        if (discountedTotal.compareTo(BigDecimal.ZERO) < 0) {
            discountedTotal = BigDecimal.ZERO;
        }

        return discountedTotal.setScale(2, RoundingMode.HALF_UP);
    }

    public Order getCurrentOrder(UUID userId) {
        Optional<Order> currentOrder = orderRepository.getLastOrderByUserId(userId);
        if (currentOrder.isEmpty()) {
            throw new CartNotExisting("Кошницата е празна!");
        }
        return currentOrder.get();
    }

    public Page<Order> getOrdersForProfile(UUID userId, Pageable pageable) {
        return orderRepository.findByUserIdAndOrderStatusNot(userId, OrderStatus.PENDING, pageable);
    }

    public Order getOrderDetails(UUID userId, UUID orderId) {
        return orderRepository.findWithDetailsByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new CartNotExisting("Поръчката не е намерена."));
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
        orderRepository.save(order);
        eventPublisher.publishEvent(new OrderCompletedEvent(order));
    }

    @Transactional
    public void cancelPendingOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new CartNotExisting("Поръчката не е намерена."));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Само незавършена поръчка може да бъде отменена.");
        }

        voucherService.releasePendingVoucher(order.getVoucher());
        order.setVoucher(null);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public List<Order> allCompletedOrders() {
        return orderRepository.getOrderByOrderStatus(OrderStatus.COMPLETED);
    }

    public List<Order> allConfirmedOrdersBeforeTwoDays() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        return orderRepository.getAllOrderByOrderStatusAndOrderDateBefore(OrderStatus.ORDER_CONFIRMED, twoDaysAgo);
    }

    public List<Order> allDeliveredOrdersBefore30min() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        return orderRepository.getAllOrderByOrderStatusAndOrderDateBefore(OrderStatus.DELIVERED, thirtyMinutesAgo);
    }

    public List<Order> allPendingOrdersBeforeOneDay() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        return orderRepository.getAllOrderByOrderStatusAndOrderDateBefore(OrderStatus.PENDING, oneDayAgo);
    }




}
