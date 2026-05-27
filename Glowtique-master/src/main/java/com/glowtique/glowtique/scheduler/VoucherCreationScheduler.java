package com.glowtique.glowtique.scheduler;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderStatus;
import com.glowtique.glowtique.order.repository.OrderRepository;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.voucher.model.Voucher;
import com.glowtique.glowtique.voucher.model.VoucherType;
import com.glowtique.glowtique.voucher.repository.VoucherRepository;
import com.glowtique.glowtique.voucher.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class VoucherCreationScheduler {

    private OrderService orderService;
    private VoucherRepository voucherRepository;
    private VoucherService voucherService;

    @Autowired
    public VoucherCreationScheduler(OrderService orderService, VoucherRepository voucherRepository, VoucherService voucherService) {
        this.orderService = orderService;
        this.voucherRepository = voucherRepository;
        this.voucherService = voucherService;
    }

    private void chanceToCreate(Order order) {
        User user = order.getUser();

        long numOfCompletedOrders = user.getOrders().stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .count();

        if (numOfCompletedOrders == user.getVouchers().size()) {
            return;
        }


    }


    @Scheduled(fixedRate = 4500000)
    public void createVoucher() {
        log.info("Casting.");
        List<Order> completedOrders = orderService.allCompletedOrders();

        for (Order order : completedOrders) {
            chanceToCreate(order);
        }

    }

}
