package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderItem;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.payment.service.PaymentService;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.Country;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.glowtique.glowtique.user.model.User;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
public class PaymentControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private OrderService orderService;

    @Test
    void gettingPaymentPageAuthorised() throws Exception {
        User user = new User();
        user.setCountry(Country.BULGARIA);
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.TEN);
        product.setQuantity(2);
        Order order = Order.builder()
                .user(user).shippingAddress("asd")
                .build();
        OrderItem orderItem = OrderItem.builder()
                .quantity(product.getQuantity())
                .product(product)
                .order(order)
                .priceAtPurchase(BigDecimal.valueOf(230)).build();
        order.setOrderItems(List.of(orderItem));

        Cart cart = new Cart();
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(product.getQuantity())
                .build();
        CartItem cartItem2 = CartItem.builder()
                .cart(cart)
                .quantity(product.getQuantity())
                .product(product)
                .build();
        cart.setCartItems(List.of(cartItem, cartItem2));
        cart.setUser(user);
        user.setCart(cart);
        user.setOrders(Set.of(Order.builder().shippingAddress("asd").build()));
        user.setId(UUID.randomUUID());
        when(userService.getUserById(any())).thenReturn(user);
        when(orderService.getCurrentOrder(any())).thenReturn(order);

        AuthenticationMetadata auth = new AuthenticationMetadata(user.getId(), "test", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/payment").with(user(auth));

        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("currentOrder"));
    }
    @Test
    @WithMockUser
    void gettingPaymentPageUnauthorized() throws Exception {
        MockHttpServletRequestBuilder request = get("/payment");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }
    @Test
    void gettingOrderConfirmationPageAuthorised() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());

        AuthenticationMetadata auth = new AuthenticationMetadata(user.getId(), "test", "password", UserRole.USER, true);
        MockHttpServletRequestBuilder request = get("/order-confirmation").with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("order-confirmation"));

    }

    @Test
    @WithMockUser
    void gettingOrderConfirmationPageUnauthorized() throws Exception {
        MockHttpServletRequestBuilder request = get("/order-confirmation");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }
}
