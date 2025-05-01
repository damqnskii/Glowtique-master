package com.glowtique.glowtique.scheduler;

import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.repository.OrderRepository;
import com.glowtique.glowtique.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.glowtique.glowtique.order.model.OrderStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderSchedulerUTest {
    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderDeliveryScheduler orderDeliveryScheduler;



    @Test
    void deliverOrder_shouldUpdateOrderStatusToDelivered() {
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.ORDER_CONFIRMED);
        Order order2 = new Order();
        order2.setOrderStatus(OrderStatus.ORDER_CONFIRMED);

        List<Order> confirmedOrders = List.of(order1, order2);

        when(orderService.allConfirmedOrdersBeforeTwoDays()).thenReturn(confirmedOrders);

        orderDeliveryScheduler.deliverOrder();

        for (Order order : confirmedOrders) {
            verify(orderRepository).save(order);
            assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
        }
    }

    @Test
    void deliverOrder_shouldNotUpdateWhenNoOrders() {
        when(orderService.allConfirmedOrdersBeforeTwoDays()).thenReturn(Collections.emptyList());

        orderDeliveryScheduler.deliverOrder();

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void awaitingPickUp_shouldUpdateOrderStatusToAwaitingPickup() {
        Order order1 = new Order();
        order1.setOrderStatus(OrderStatus.DELIVERED);
        Order order2 = new Order();
        order2.setOrderStatus(OrderStatus.DELIVERED);

        List<Order> deliveredOrders = Arrays.asList(order1, order2);

        when(orderService.allDeliveredOrdersBefore30min()).thenReturn(deliveredOrders);

        orderDeliveryScheduler.awaitingPickUp();

        for (Order order : deliveredOrders) {
            verify(orderRepository).save(order);
            assertEquals(OrderStatus.AWAITING_PICKUP, order.getOrderStatus());
        }
    }

    @Test
    void awaitingPickUp_shouldNotUpdateWhenNoOrders() {
        when(orderService.allDeliveredOrdersBefore30min()).thenReturn(Collections.emptyList());

        orderDeliveryScheduler.awaitingPickUp();

        verify(orderRepository, never()).save(any(Order.class));
    }

}
