package com.glowtique.glowtique.voucher.listener;

import com.glowtique.glowtique.order.event.OrderCompletedEvent;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.voucher.model.VoucherType;
import com.glowtique.glowtique.voucher.service.VoucherService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class VoucherOnCompletedListener {

    private final VoucherService voucherService;

    public VoucherOnCompletedListener(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event) {

        Order order = event.getOrder();
        User user = order.getUser();

        if (!voucherService.isEqualToCompletedOrders(user)) {
            return;
        }

        int percentOrPrice = ThreadLocalRandom.current().nextInt(6);

        if (percentOrPrice == 1) {
            if (order.getTotalPrice().compareTo(BigDecimal.valueOf(250.00)) <= 0) {
                voucherService.createPercentageVoucher(user, BigDecimal.valueOf(15.000), VoucherType.FIFTEEN_PERCENT);
            } else {
                voucherService.createPercentageVoucher(user, BigDecimal.valueOf(10.000), VoucherType.TEN_PERCENT);
            }
        } else if (percentOrPrice == 2) {
            if (order.getTotalPrice().compareTo(BigDecimal.valueOf(500.00)) <= 0) {
                voucherService.createPriceVoucher(user, BigDecimal.valueOf(50.0), VoucherType.FIFTY_BGN);
            } else {
                voucherService.createPriceVoucher(user, BigDecimal.valueOf(20.000), VoucherType.TWENTY_BGN);
            }
        }

    }
}
