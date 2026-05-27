package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderMethod;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.voucher.service.VoucherService;
import com.glowtique.glowtique.web.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final VoucherService voucherService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, VoucherService voucherService) {
        this.userService = userService;
        this.orderService = orderService;
        this.voucherService = voucherService;
    }

    @GetMapping("/checkout")
    public ModelAndView getOrder(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("checkout");
        modelAndView.addObject("user", user);
        Cart cart = user.getCart();
        modelAndView.addObject("cart", cart);

        List<CartItem> cartItems = cart.getCartItems();
        modelAndView.addObject("voucher", cart.getUsedVoucher());
        modelAndView.addObject("cartItems", cartItems);
        modelAndView.addObject("orderRequest", new OrderRequest());
        modelAndView.addObject("orderMethods", OrderMethod.values());

        return modelAndView;
    }

    @PostMapping("/checkout/create")
    public ModelAndView createOrder(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                    @Valid @ModelAttribute OrderRequest orderRequest, BindingResult bindingResult) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());
        Cart cart = user.getCart();
        List<CartItem> cartItems = cart.getCartItems();
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("checkout");
            modelAndView.addObject("orderRequest", orderRequest);
            modelAndView.addObject("orderMethods", OrderMethod.values());
            modelAndView.addObject("user", user);
            modelAndView.addObject("cart", cart);
            modelAndView.addObject("cartItems", cartItems);

            return modelAndView;
        }

        orderService.createOrder(orderRequest, user, cart);


        return new ModelAndView("redirect:/payment");

    }

    @GetMapping("/orders")
    public ModelAndView getOrders(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());
        int currentPage = Math.max(page, 0);
        int pageSize = size <= 0 ? 12 : Math.min(size, 48);
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> ordersPage = orderService.getOrdersForProfile(user.getId(), pageable);

        ModelAndView modelAndView = new ModelAndView("orders");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", ordersPage.getContent());
        modelAndView.addObject("currentPage", ordersPage.getNumber());
        modelAndView.addObject("totalPages", ordersPage.getTotalPages());
        modelAndView.addObject("totalOrders", ordersPage.getTotalElements());
        modelAndView.addObject("pageSize", pageSize);
        return modelAndView;
    }

    @GetMapping("/orders/{orderId}")
    public ModelAndView getOrderDetails(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                        @PathVariable UUID orderId) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());
        Order order = orderService.getOrderDetails(user.getId(), orderId);
        BigDecimal itemsSubtotal = order.getOrderItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingPrice = itemsSubtotal.compareTo(BigDecimal.valueOf(200)) < 0
                ? BigDecimal.valueOf(7)
                : BigDecimal.ZERO;
        BigDecimal discountAmount = itemsSubtotal.add(shippingPrice).subtract(order.getTotalPrice());
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            discountAmount = BigDecimal.ZERO;
        }

        ModelAndView modelAndView = new ModelAndView("order-details");
        modelAndView.addObject("user", user);
        modelAndView.addObject("order", order);
        modelAndView.addObject("orderItems", order.getOrderItems());
        modelAndView.addObject("itemsSubtotal", itemsSubtotal);
        modelAndView.addObject("shippingPrice", shippingPrice);
        modelAndView.addObject("discountAmount", discountAmount);
        return modelAndView;
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ModelAndView cancelPendingOrder(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                           @PathVariable UUID orderId) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        orderService.cancelPendingOrder(authenticationMetadata.getUserId(), orderId);
        return new ModelAndView("redirect:/cart");
    }

}
