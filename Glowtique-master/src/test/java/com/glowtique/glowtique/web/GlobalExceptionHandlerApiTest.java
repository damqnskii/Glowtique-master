package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.exception.*;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.AdminRequest;
import com.glowtique.glowtique.web.dto.EditProfileRequest;
import com.glowtique.glowtique.web.dto.OrderRequest;
import com.glowtique.glowtique.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import com.glowtique.glowtique.user.model.User;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserController userController;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private CartService cartService;
    @MockitoBean
    private OrderController orderController;
    @MockitoBean
    private CartController cartController;

    @Test
    void testHandleUnauthorisedException() throws Exception {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setRole(UserRole.USER);
        doThrow(new UnauthorizedException("Unauthorised")).when(userController).editRole(UUID.randomUUID(), adminRequest);

        MockHttpServletRequestBuilder request = get("/admin-dashboard");

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testUserNotExistingException() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setQuantity(2);
        AuthenticationMetadata auth = new AuthenticationMetadata(user.getId(), "test@gmail.com", "password", UserRole.USER, true);
        doThrow(new UserNotExisting("Not existing user")).when(cartController)
                .addItemToCart(auth, product.getId(), product.getQuantity());

        MockHttpServletRequestBuilder request = post("/cart/add/" + product.getId())
                .with(csrf())
                .with(user(auth));

        mockMvc.perform(request.with(user(auth)))
                .andExpect(status().is4xxClientError());

    }
    @Test
    @WithMockUser
    void testAlreadyRegisteredEmail() throws Exception {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@gmail.com");
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@gmail.com");

        doThrow(new AlreadyRegEmailException("Email already registered")).when(userService).register(registerRequest);
        MockHttpServletRequestBuilder request = post("/register").with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }


}
