package com.glowtique.glowtique.scheduler;

import com.glowtique.glowtique.order.repository.OrderRepository;
import com.glowtique.glowtique.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.glowtique.glowtique.order.model.Order;

import java.util.List;

@Component
@Slf4j
public class OrderCleanupScheduler {
    private OrderRepository orderRepository;
    private OrderService orderService;

    @Autowired
    public OrderCleanupScheduler(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanup() {
        List<Order> allPendingOrdersOneDayAgo = orderService.allPendingOrdersBeforeOneDay();

        if (allPendingOrdersOneDayAgo.isEmpty()) {
            log.info("There are not any orders to be cleaned up");
            return;
        }

        orderRepository.deleteAll(allPendingOrdersOneDayAgo);
        log.info("{} pending orders were cleaned up", allPendingOrdersOneDayAgo.size());
    }
}
