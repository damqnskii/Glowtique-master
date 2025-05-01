package com.glowtique.glowtique.brand;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.brand.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrandServiceUTest {
    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;
    private Brand brand;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(UUID.randomUUID());
        brand.setName("Dior");
        brand.setLogo("photo");
        brand.setDescription("This is a unique brand");
    }

    @Test
    void gettingRuntimeExceptionWhenBrandIdNotFound() {
        assertThrows(RuntimeException.class, () -> brandService.getBrandById(UUID.randomUUID()));
    }
    @Test
    void correctGettingBrandById() {
        when(brandRepository.findBrandById(brand.getId())).thenReturn(Optional.of(brand));

        Brand result = brandService.getBrandById(brand.getId());

        assertEquals(brand, result);
    }
    @Test
    void gettingAllBrands() {

        when(brandRepository.findAll()).thenReturn(List.of(new Brand(), new Brand(), new Brand()));
        List<Brand> result = brandService.getAllBrands();

        assertEquals(3, result.size());

    }


}
