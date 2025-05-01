package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.exception.NotEnoughProductStock;
import com.glowtique.glowtique.order.model.OrderMethod;
import com.glowtique.glowtique.order.service.OrderService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private OrderService orderService;

    @Test
    void GetOrdersPageAuthenticated() throws Exception {
        User mockUser = new User();
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(any())).thenReturn(mockUser);

        mockUser.setId(userId);
        mockUser.setCart(new Cart());

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/checkout").with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attributeExists("orderRequest"))
                .andExpect(model().attributeExists("orderMethods"))
                .andExpect(model().attributeExists("cartItems"));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @WithMockUser
    void getOrderPageUnauthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/checkout");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void getOrdersPageAuthenticated() throws Exception {
        User mockUser = new User();
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(any())).thenReturn(mockUser);

        mockUser.setId(userId);

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/orders").with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("orders"));
        verify(userService, times(1)).getUserById(userId);
    }
    @Test
    @WithMockUser
    void getOrdersPageUnauthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/orders");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

    }
    @Test
    @WithMockUser
    void postRequestUnauthorised() throws Exception {
        MockHttpServletRequestBuilder request = post("/checkout/create");
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void postCreateOrder() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata authenticationMetadata = new AuthenticationMetadata(userId, "test@gmail.com", "password", UserRole.USER, true);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("0898946872");
        user.setId(userId);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setPostCode("12345");
        orderRequest.setFirstName(user.getFirstName());
        orderRequest.setLastName(user.getLastName());
        orderRequest.setPhoneNumber(user.getPhoneNumber());
        orderRequest.setShippingAddress("asd");
        orderRequest.setTown("Varna");
        orderRequest.setOrderMethod(OrderMethod.SPEEDY_POST);
        when(userService.getUserById(any())).thenReturn(user);
        user.setCart(Cart.builder().cartItems(List.of(new CartItem())).build());

        MockHttpServletRequestBuilder request = post("/checkout/create")
                .formField("firstName", orderRequest.getFirstName())
                .formField("lastName", orderRequest.getLastName())
                .formField("postCode", orderRequest.getPostCode())
                .formField("phoneNumber", orderRequest.getPhoneNumber())
                .formField("shippingAddress", orderRequest.getShippingAddress())
                .formField("town", orderRequest.getTown())
                .formField("orderMethod", orderRequest.getOrderMethod().toString())
                .with(user(authenticationMetadata)).with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }
    @Test
    @WithMockUser
    void postCreateOrderUnauthenticated() throws Exception {
        MockHttpServletRequestBuilder request = post("/checkout/create").with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    void postCreateOrderWrongRequestInfo() throws Exception {
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata authenticationMetadata = new AuthenticationMetadata(userId, "test@gmail.com", "password", UserRole.USER, true);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("0898946872");
        user.setId(userId);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setPostCode("12345");
        orderRequest.setFirstName(user.getFirstName());
        orderRequest.setLastName(user.getLastName());
        orderRequest.setPhoneNumber(user.getPhoneNumber());
        orderRequest.setShippingAddress("asd");
        orderRequest.setTown("Varna");
        orderRequest.setOrderMethod(OrderMethod.SPEEDY_POST);
        when(userService.getUserById(any())).thenReturn(user);
        user.setCart(Cart.builder().cartItems(List.of(new CartItem())).build());

        when(orderService.createOrder(orderRequest, user, user.getCart())).thenThrow(new NotEnoughProductStock("Not enough products in your cart"));

        MockHttpServletRequestBuilder request = post("/checkout/create")
                .formField("firstName", orderRequest.getFirstName())
                .formField("lastName", orderRequest.getLastName())
                .formField("postCode", orderRequest.getPostCode())
                .formField("phoneNumber", orderRequest.getPhoneNumber())
                .formField("shippingAddress", orderRequest.getShippingAddress())
                .formField("town", orderRequest.getTown())
                .formField("orderMethod", orderRequest.getOrderMethod().toString())
                .with(user(authenticationMetadata)).with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout"));
    }
}
