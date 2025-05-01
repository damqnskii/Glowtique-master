package com.glowtique.glowtique.web;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.service.BrandService;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.service.ProductService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import com.glowtique.glowtique.user.model.User;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private WishlistItemService wishlistItemService;

    @MockitoBean
    private BrandService brandService;

    @Test
    @WithMockUser
    void getProductsPageUnAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/products");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getProductsPageAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        when(userService.getUserById(any())).thenReturn(user);

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/products").with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("wishListed"))
                .andExpect(model().attributeExists("products"));
    }
    @Test
    void getProductDetailsAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setBrand(new Brand());

        when(userService.getUserById(any())).thenReturn(user);
        when(productService.getProductById(any())).thenReturn(product);

        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test", "password", UserRole.USER, true);
        MockHttpServletRequestBuilder request = get("/product/" + product.getId()).with(user(auth));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attributeExists("wishListed"))
                .andExpect(model().attributeExists("relatedProducts"));
    }
    @Test
    @WithMockUser
    void getProductDetailsUnAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/product/" + UUID.randomUUID());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    void getProductBrandAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        Brand brand = new Brand();
        brand.setId(UUID.randomUUID());
        when(userService.getUserById(any())).thenReturn(user);
        when(brandService.getBrandById(any())).thenReturn(brand);
        AuthenticationMetadata auth = new AuthenticationMetadata(userId, "test", "password", UserRole.USER, true);

        MockHttpServletRequestBuilder request = get("/product-brand/" + brand.getId()).with(user(auth));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("product-brand"))
                .andExpect(model().attributeExists("wishListed"));
    }

    @Test
    @WithMockUser
    void getProductBrandUnAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/product-brand/" + UUID.randomUUID());
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }
}
