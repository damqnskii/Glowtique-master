package com.glowtique.glowtique.scheduler;

import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.order.repository.OrderRepository;
import com.glowtique.glowtique.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderDeliveryScheduler {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderDeliveryScheduler(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @Scheduled(cron = "0 0 9 * * MON-FRI")
    public void deliverOrder() {
        List<Order> orders = orderService.allConfirmedOrdersBeforeTwoDays();

        if (orders.isEmpty()) {
            log.info("There are not any orders that needs to be delivered");
            return;
        }

        for (Order order : orders) {
            order.setOrderStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        }

    }
    @Scheduled(cron = "0 30 9 * * MON-FRI")
    public void awaitingPickUp() {
        List<Order> deliveredOrders = orderService.allDeliveredOrdersBefore30min();

        if (deliveredOrders.isEmpty()) {
            log.info("There are not any orders that needs to be picked up");
            return;
        }

        for (Order order : deliveredOrders) {
            order.setOrderStatus(OrderStatus.AWAITING_PICKUP);
            orderRepository.save(order);
        }
    }
}
