package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.exception.CartNotExisting;
import com.glowtique.glowtique.order.model.OrderMethod;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.glowtique.glowtique.user.model.User;

import java.util.List;

@Controller
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
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
    public ModelAndView getOrders(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView("orders");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", userService.getConfirmedOrders(user));
        return modelAndView;
    }

}
