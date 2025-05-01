package com.glowtique.glowtique.wishlistitem;

import com.glowtique.glowtique.exception.ProductNotfoundException;
import com.glowtique.glowtique.exception.UnauthorizedException;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.repository.UserRepository;
import com.glowtique.glowtique.web.dto.ProductRequest;
import com.glowtique.glowtique.wishlistitem.model.WishlistItem;
import com.glowtique.glowtique.wishlistitem.repository.WishlistRepository;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistitemUTest {
    @Mock
    private WishlistRepository wishlistRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WishlistItemService wishlistItemService;
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .wishlistItems(new HashSet<>())
                .build();
        product = Product.builder()
                .name("Sauvage Dior")
                .id(UUID.randomUUID())
                .image("asdasd")
                .price(BigDecimal.valueOf(230))
                .volume(50)
                .quantity(2)
                .description("fragrance for men")
                .build();

    }

    @Test
    void UnauthorizedExceptionWhenAddingWishlistItem() {
        user.setId(null);
        assertThrows(UnauthorizedException.class, () -> wishlistItemService.addToWishlist(product.getId(), user));
    }

    @Test
    void gettingProductNotFoundExceptionWhenProductNotExists() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotfoundException.class, () -> wishlistItemService.addToWishlist(product.getId(), user));
    }

    @Test
    void addingProductToWishlistWhenTheProductAlreadyWishlisted() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(true);

        boolean result = wishlistItemService.addToWishlist(product.getId(), user);

        assertFalse(result);
    }

    @Test
    void creatingAndAddingCorrectlyToTheWishlist() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(wishlistRepository.existsByUserAndProduct(user, product)).thenReturn(false);

        boolean result = wishlistItemService.addToWishlist(product.getId(), user);

        assertTrue(result);
    }

    @Test
    void mappingWishlistItemToProductRequest() {
        Product product1 = Product.builder()
                .id(UUID.randomUUID())
                .name("Hugo Boss")
                .image("sadaf")
                .price(BigDecimal.TEN)
                .volume(100)
                .quantity(3)
                .description("cool fragrances")
                .build();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
        WishlistItem wishlistItem = WishlistItem.builder()
                .product(product)
                .user(user)
                .isLiked(true)
                .build();
        WishlistItem wishlistItem1 = WishlistItem.builder()
                .product(product1)
                .user(user)
                .isLiked(false)
                .build();
        wishlistItemService.addToWishlist(product.getId(), user);
        wishlistItemService.addToWishlist(product1.getId(), user);
        when(wishlistRepository.findTop15ByUser(user)).thenReturn(List.of(wishlistItem, wishlistItem1));

        List<ProductRequest> result = wishlistItemService.getWishListItemsAsProductRequest(user);

        assertNotNull(result);
        assertEquals(product1.getId(), result.get(1).getId());
        assertEquals(2, result.size());
        assertEquals("Hugo Boss", result.get(1).getName());
        assertEquals(BigDecimal.TEN, result.get(1).getPrice());
        assertEquals("sadaf", result.get(1).getImage());
        assertEquals("cool fragrances", result.get(1).getDescription());
        assertEquals(100, result.get(1).getVolume());
        assertEquals(3, result.get(1).getQuantity());
    }

    @Test
    void gettingExceptionWhenTryingToRemoveWishlistItemWhichNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());
        assertThrows(ProductNotfoundException.class, () -> wishlistItemService.removeFromWishlist(product.getId(), user));
    }

    @Test
    void removingCorrectlyFromWishlist() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        boolean result = wishlistItemService.removeFromWishlist(product.getId(), user);

        assertTrue(result);
        verify(wishlistRepository).deleteWishlistItemByUserAndProduct(user, product);
    }

    @Test
    void gettingWishlistItemsCorrectly() {
        WishlistItem wishlistItem = WishlistItem.builder()
                .product(product)
                .user(user)
                .isLiked(true)
                .build();
        when(wishlistRepository.findTop15ByUser(user)).thenReturn(List.of(wishlistItem));

        List<WishlistItem> result = wishlistItemService.wishListedItems(user);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Sauvage Dior", result.get(0).getProduct().getName());
    }

    @Test
    void returnsEmptyWishList() {
        when(wishlistRepository.findTop15ByUser(user)).thenReturn(new ArrayList<>());

        List<WishlistItem> result = wishlistItemService.wishListedItems(user);

        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(wishlistRepository, times(1)).findTop15ByUser(user);

    }

}
