package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }
    @GetMapping("/cart")
    public ModelAndView getCartPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView modelAndView = new ModelAndView("cart");
        modelAndView.addObject("user", userService.getUserById(authenticationMetadata.getUserId()));

        Cart cart = cartService.getCartByUser(authenticationMetadata.getUserId());
        modelAndView.addObject("cart", cart);

        return modelAndView;
    }

    @PostMapping("/cart/add/{productId}")
    public ModelAndView addItemToCart(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                              @PathVariable UUID productId,
                                              @RequestParam int quantity) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        UUID userId = authenticationMetadata.getUserId();
        cartService.addItemToCart(userId, productId, quantity);
        return new ModelAndView("redirect:/cart");
    }

    @PostMapping("/cart/remove/{productId}")
    public ModelAndView removeItemFromCart(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                        @PathVariable UUID productId) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        UUID userId = authenticationMetadata.getUserId();
        cartService.removeItemFromCart(userId, productId);
        return new ModelAndView("redirect:/cart");
    }

    @PutMapping("/cart/update-quantity/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                                              @PathVariable UUID productId,
                                                              @RequestParam("quantity") int quantity) {
        if (authenticationMetadata.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User is not authenticated!"));
        }
        Cart updateCart = cartService.updateQuantity(authenticationMetadata.getUserId(), productId, quantity);

        Map<String, Object> response = new HashMap<>();
        response.put("totalPrice", updateCart.getTotalPrice());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}
