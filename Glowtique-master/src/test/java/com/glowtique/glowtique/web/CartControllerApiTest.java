package com.glowtique.glowtique.web;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.service.ProductService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private UserService userService;


    @Test
    void getCartPageAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        User mockedUser = new User();
        CartItem cartItem = new CartItem();
        mockedUser.setId(userId);
        Product product = new Product();
        Brand brand = new Brand();
        brand.setName("Hugo");
        product.setId(UUID.randomUUID());
        product.setName("test");
        product.setImage("asd");
        product.setBrand(brand);
        product.setDescription("Test");
        product.setPrice(BigDecimal.valueOf(230));
        product.setQuantity(4);
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setCartItems(List.of(cartItem));
        cartItem.setProduct(product);
        cartItem.setQuantity(4);
        cartItem.setCart(cart);
        cart.setUser(mockedUser);

        mockedUser.setCart(cart);

        when(userService.getUserById(any())).thenReturn(mockedUser);
        when(cartService.getCartByUser(mockedUser.getId())).thenReturn(cart);

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "User123", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/cart").with(user(auth));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"));
    }

    @Test
    @WithMockUser
    void getCartPageNotAuthorised() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    void postAddProductToCartUnauthorised() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID());

        MockHttpServletRequestBuilder request = post("/cart/add/" + product.getId())
                .param("quantity", String.valueOf(product.getQuantity()));

        mockMvc.perform(request.with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    void postAddProductToCartAuthenticated() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());

        Product product = new Product();
        product.setId(UUID.randomUUID());

        AuthenticationMetadata auth = new AuthenticationMetadata(user.getId(), "User123", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = post("/cart/add/" + product.getId())
                .param("quantity", String.valueOf(product.getQuantity()))
                .with(user(auth)).with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    @WithMockUser
    void postRemoveProductFromCartUnauthorised() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID());

        MockHttpServletRequestBuilder request = post("/cart/remove/" + product.getId())
                .param("quantity", String.valueOf(product.getQuantity()));

        mockMvc.perform(request.with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    void postRemoveProductFromCartAuthenticated() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());

        Product product = new Product();
        product.setId(UUID.randomUUID());

        AuthenticationMetadata auth = new AuthenticationMetadata(user.getId(), "User123", "123123", UserRole.USER, true);

        MockHttpServletRequestBuilder request = post("/cart/remove/" + product.getId())
                .param("quantity", String.valueOf(product.getQuantity()))
                .with(user(auth)).with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }
    @Test
    void updateQuantityUnauthorised() throws Exception {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setQuantity(2);

        User user = new User();
        user.setId(UUID.randomUUID());

        Cart cart = new Cart();

        when(cartService.updateQuantity(any(UUID.class), any(UUID.class), anyInt())).thenReturn(cart);

        MockHttpServletRequestBuilder request = put("/cart/update-quantity/" + product.getId())
                .param("quantity", String.valueOf(1))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateQuantityAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        User mockedUser = new User();
        CartItem cartItem = new CartItem();
        mockedUser.setId(userId);
        Product product = new Product();
        Brand brand = new Brand();
        brand.setName("Hugo");
        product.setId(UUID.randomUUID());
        product.setName("test");
        product.setImage("asd");
        product.setBrand(brand);
        product.setDescription("Test");
        product.setPrice(BigDecimal.valueOf(230));
        product.setQuantity(4);
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setCartItems(List.of(cartItem));
        cartItem.setProduct(product);
        cartItem.setQuantity(4);
        cartItem.setCart(cart);
        cart.setUser(mockedUser);

        mockedUser.setCart(cart);

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "User123", "123123", UserRole.USER, true);

        when(cartService.updateQuantity(userId, product.getId(), 1)).thenReturn(cart);

        mockMvc.perform(put("/cart/update-quantity/" + product.getId())
                        .param("quantity", String.valueOf(1))
                        .with(user(auth))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(cart.getTotalPrice()))
                .andExpect(jsonPath("$.status").value("success"));
    }

}
