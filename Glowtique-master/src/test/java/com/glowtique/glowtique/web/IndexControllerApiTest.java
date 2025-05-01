package com.glowtique.glowtique.web;

import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.service.UserService;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import com.glowtique.glowtique.user.model.User;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    @Test
    @WithMockUser
    void getIndexPage_CorrectlyReturnIndexPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getIndexPage_NotCorrectly() throws Exception {
        MockHttpServletRequestBuilder request = get("/");
        mockMvc.perform(request)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void getRegisterPage_CorrectlyReturnRegisterPage() throws Exception {
        MockHttpServletRequestBuilder request = get("/register");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

    }

    @Test
    void getHomePage_Correctly() throws Exception {
        when(userService.getUserById(any())).thenReturn(new User());
        MockHttpServletRequestBuilder request = get("/home");

        UUID userId = UUID.randomUUID();

        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "User123", "123123", UserRole.USER, true);

        mockMvc.perform(request.with(user(principal)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("home"));

    }
    @Test
    @WithMockUser
    void getHomePageNoAuthRedirectionToLogin() throws Exception {
        MockHttpServletRequestBuilder request = get("/home");
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void getLoginPageCorrectly() throws Exception {
        MockHttpServletRequestBuilder request = get("/login").with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
