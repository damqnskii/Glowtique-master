package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.payment.model.PaymentMethod;
import com.glowtique.glowtique.payment.service.PaymentService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.voucher.model.Voucher;
import com.glowtique.glowtique.voucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;

@Controller
public class PaymentController {
    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final VoucherService voucherService;


    @Autowired
    public PaymentController(UserService userService, OrderService orderService, PaymentService paymentService, VoucherService voucherService) {
        this.userService = userService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.voucherService = voucherService;
    }

    @GetMapping("/payment")
    public ModelAndView getPaymentProcessing(@AuthenticationPrincipal AuthenticationMetadata  authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());
        Order currentOrder = orderService.getCurrentOrder(user.getId());
        BigDecimal itemsSubtotal = currentOrder.getOrderItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingPrice = itemsSubtotal.compareTo(BigDecimal.valueOf(200)) < 0 ? BigDecimal.valueOf(7) : BigDecimal.ZERO;
        BigDecimal discountAmount = itemsSubtotal.add(shippingPrice).subtract(currentOrder.getTotalPrice());
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            discountAmount = BigDecimal.ZERO;
        }

        ModelAndView modelAndView = new ModelAndView("payment");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orderItems", currentOrder.getOrderItems());
        modelAndView.addObject("currentOrder", currentOrder);
        modelAndView.addObject("itemsSubtotal", itemsSubtotal);
        modelAndView.addObject("shippingPrice", shippingPrice);
        modelAndView.addObject("discountAmount", discountAmount);
        modelAndView.addObject("voucher", currentOrder.getVoucher());
        return modelAndView;
    }
    @PostMapping("/payment/create")
    public String processPayment(@RequestParam("payment") String paymentMethodStr,
                                 @RequestParam(required = false) String cardNumber,
                                 @RequestParam(required = false) String expiryDate,
                                 @RequestParam(required = false) String cvc,
                                 @RequestParam(required = false) String giftCardNumber,
                                 @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return "redirect:/login";
        }
        System.out.println("paymentMethodStr: " + paymentMethodStr);

        User user = userService.getUserById(authenticationMetadata.getUserId());
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);
        return paymentService.processPayment(paymentMethod, cardNumber, expiryDate, cvc, giftCardNumber, user);
    }

    @GetMapping("/order-confirmation")
    public ModelAndView getConfirmationOrder(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());

        return new ModelAndView("order-confirmation");
    }
}
