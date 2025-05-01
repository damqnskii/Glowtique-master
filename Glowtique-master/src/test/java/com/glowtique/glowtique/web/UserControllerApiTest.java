package com.glowtique.glowtique.web;

import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private WishlistItemService wishlistItemService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAdminDashboardWhenUserHasAdminRole() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/admin-dashboard");

        mockMvc.perform(request)
                .andExpect(view().name("admin-dashboard"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("adminRequest"));
    }
    @Test
    void getProfileMenuCorrectly() throws Exception {
        when(userService.getUserById(any())).thenReturn(new User());

        UUID userId = UUID.randomUUID();

        AuthenticationMetadata authenticationMetadata = new AuthenticationMetadata(userId, "test@gmail.com", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profile").with(user(authenticationMetadata));
        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
        verify(userService, times(1)).getUserById(userId);
    }
    @Test
    @WithMockUser
    void getProfileMenuNotAutheticated() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/profile");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void getEditProfileCorrectly() throws Exception {
        when(userService.getUserById(any())).thenReturn(new User());
        UUID userId = UUID.randomUUID();

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test@gmail.com", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/edit-profile").with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("editProfileRequest"));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @WithMockUser
    void getEditProfileNotAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/edit-profile");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void getWishlistCorrectly() throws Exception {
        when(userService.getUserById(any())).thenReturn(new User());

        UUID userId = UUID.randomUUID();

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test@gmail.com", "password", UserRole.USER, true);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/wishlist").with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("wishlist"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("wishlistItems"));
    }

    @Test
    @WithMockUser
    void getWishlistNotAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/wishlist");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void getLoyaltyPointsPageCorrectly() throws Exception {
        when(userService.getUserById(any())).thenReturn(new User());
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test@gmail.com", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/loyalty-points").with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("loyalty-points"));
    }

    @Test
    @WithMockUser
    void getLoyaltyPointsNotAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/loyalty-points");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
