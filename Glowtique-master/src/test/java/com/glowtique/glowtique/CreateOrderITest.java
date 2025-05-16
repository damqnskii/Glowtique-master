package com.glowtique.glowtique;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.model.UserGender;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.RegisterRequest;
import com.glowtique.glowtique.wishlistitem.repository.WishlistRepository;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import com.glowtique.glowtique.user.model.User;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest
public class CreateOrderITest {

    @Autowired
    private UserService userService;
    @Autowired
    private WishlistItemService wishlistItemService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private com.glowtique.glowtique.user.repository.UserRepository userRepository;

    @Test
    void testAddToWishlistProductCorrectly() {
        String randomSuffix = UUID.randomUUID().toString().substring(0,8);
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Damian");
        registerRequest.setLastName("Apostolov");
        registerRequest.setEmail("test@" + randomSuffix + ".com");
        registerRequest.setPassword("asdasd");
        registerRequest.setConfirmPassword("asdasd");
        registerRequest.setGender(UserGender.MALE);
        registerRequest.setBirthday(LocalDate.now().minusYears(21));

        User user = userService.register(registerRequest);

        assertNotNull(user.getCart());
        assertTrue(user.isActive());

        Brand brand = new Brand();
        brand.setName("Dior");
        brand.setLogo("dior");
        brand.setDescription("description");
        brandRepository.save(brand);

        Product product = Product.builder()
                .brand(brand)
                .name("Dior")
                .price(BigDecimal.valueOf(230))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .quantity(1)
                .build();
        productRepository.save(product);

        wishlistItemService.addToWishlist(product.getId(), user);

        assertNotNull(user.getWishlistItems());
        assertEquals(user.getWishlistItems().size(), wishlistRepository.findTop15ByUser(user).size());
        assertEquals(1, user.getWishlistItems().size());

        wishlistItemService.removeFromWishlist(product.getId(), user);

        assertNotNull(user.getWishlistItems());
        assertEquals(0, wishlistRepository.findTop15ByUser(user).size());

    }
    @Test
    @Transactional
    void addToCartCorrectly() {
        String randomSuffix = UUID.randomUUID().toString().substring(0,8);
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Damian");
        registerRequest.setLastName("Apostolov");
        registerRequest.setEmail("test@" + randomSuffix + ".com");
        registerRequest.setPassword("asdasd");
        registerRequest.setConfirmPassword("asdasd");
        registerRequest.setGender(UserGender.MALE);
        registerRequest.setBirthday(LocalDate.now().minusYears(21));

        User user = userService.register(registerRequest);

        assertNotNull(user.getCart());
        assertTrue(user.isActive());
        assertNotNull(user.getId());

        Brand brand = new Brand();
        brand.setName("Dior");
        brand.setLogo("dior");
        brand.setDescription("description");
        brandRepository.save(brand);

        Product product = Product.builder()
                .brand(brand)
                .name("Dior")
                .price(BigDecimal.valueOf(230))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .quantity(1)
                .build();
        productRepository.save(product);

        cartService.addItemToCart(user.getId(), product.getId(), 1);

        user = userRepository.findById(user.getId()).orElseThrow();

        assertEquals(1, user.getCart().getCartItems().size());
    }
}
