package com.glowtique.glowtique.web;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.service.BrandService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BrandController.class)
public class BrandControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService brandService;

    @Test
    @WithMockUser
    void getBrandsPage() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/brands");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("brands"));
    }

    @Test
    @WithMockUser
    void getSpecifiedBrand() throws Exception {
        Brand brand = new Brand();
        brand.setId(UUID.randomUUID());
        brand.setName("test");
        when(brandService.getBrandById(brand.getId())).thenReturn(brand);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/brand/" + brand.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("brand-details"));
    }
}
