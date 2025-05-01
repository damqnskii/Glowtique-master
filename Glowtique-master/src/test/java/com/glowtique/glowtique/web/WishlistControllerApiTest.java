package com.glowtique.glowtique.web;

import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(WishlistController.class)
public class WishlistControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistItemService wishlistItemService;

    @MockitoBean
    private UserService userService;


}
