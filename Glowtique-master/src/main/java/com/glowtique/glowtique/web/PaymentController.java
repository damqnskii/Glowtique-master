package com.glowtique.glowtique.web;

import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.payment.model.PaymentMethod;
import com.glowtique.glowtique.payment.service.PaymentService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.glowtique.glowtique.user.model.User;

@Controller
public class PaymentController {
    private final UserService userService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public PaymentController(UserService userService, OrderService orderService, PaymentService paymentService) {
        this.userService = userService;
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/payment")
    public ModelAndView getPaymentProcessing(@AuthenticationPrincipal AuthenticationMetadata  authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("payment");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orderItems", user.getCart().getCartItems());
        modelAndView.addObject("currentOrder", orderService.getCurrentOrder(user.getId()));
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
