package com.glowtique.glowtique.cart;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.cart.repository.CartItemRepository;
import com.glowtique.glowtique.cart.repository.CartRepository;
import com.glowtique.glowtique.cart.service.CartService;
import com.glowtique.glowtique.exception.CartNotExisting;
import com.glowtique.glowtique.exception.ProductNotfoundException;
import com.glowtique.glowtique.exception.UserNotExisting;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.glowtique.glowtique.user.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceUTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void shouldCreateCartSucessfully() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .build();
        Cart expectedCart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();
        when(cartRepository.save(any(Cart.class))).thenReturn(expectedCart);

        Cart createdCart = cartService.createCart(user);

        assertEquals(createdCart.getCartItems(), expectedCart.getCartItems());
        assertEquals(createdCart.getUser(), expectedCart.getUser());
        assertEquals(createdCart.getTotalPrice(), expectedCart.getTotalPrice());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void failedToAddItemToTheCartWhenUserIsNotFound() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotExisting.class, () -> cartService.addItemToCart(user.getId(), UUID.randomUUID(), 0));

    }

    @Test
    void failedToAddItemToTheCartWhenCartIsNotFound() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .cart(new Cart())
                .build();

        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(CartNotExisting.class, () -> cartService.addItemToCart(user.getId(), UUID.randomUUID(), 0));
    }

    @Test
    void failedToAddItemToTheCartWhenProductIsNotFound() {
        User user = User.builder()
                .id(UUID.randomUUID()).build();
        Cart cart = Cart.builder()
                .id(UUID.randomUUID()).build();
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotfoundException.class, () -> cartService.addItemToCart(user.getId(), product.getId(), 0));

    }
    @Test
    void getTheItemInTheCartByTheProductId() {
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        CartItem cartItem1 = CartItem.builder().build();
        cartItem1.setProduct(product1);
        CartItem cartItem2 = CartItem.builder().build();
        cartItem2.setProduct(product2);

        Cart cart = new Cart();
        cart.setCartItems(List.of(cartItem1, cartItem2));

        CartItem expectedItem = cartService.getCartItem(cart, product1);

        assertEquals(cartItem1, expectedItem);

    }

    @Test
    void creatingCorrectlyTheQuantityOfTheCartItemsWhenInitializingTheCart() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.TEN);
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(List.of(CartItem.builder()
                .product(product1)
                .build()));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));


        cartService.addItemToCart(user.getId(), product1.getId(), 3);

        CartItem cartItem1 = cartService.getCartItem(cart, product1);

        assertEquals(cartItem1.getTotalPrice(),  BigDecimal.valueOf(30));

    }
    @Test
    void creatingCartItemWhenTheQuantityIsZero() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.TEN);
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        cartService.addItemToCart(user.getId(), product1.getId(), 3);
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product1)
                .quantity(3)
                .build();
        cart.getCartItems().add(cartItem);

        assertEquals(cart.getCartItems().get(0).getProduct().getId(),
                cartItem.getProduct().getId());
        assertEquals(cart.getCartItems().get(0).getQuantity(),
                cartItem.getQuantity());

    }
    @Test
    void gettingExceptionWhenUserIsNotFound() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotExisting.class, () -> cartService.getCartByUser(user.getId()));
    }
    @Test
    void gettingCartByUserCorrectly() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Cart cart = new Cart();
        cart.setUser(user);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        Cart result = cartService.getCartByUser(user.getId());

        assertEquals(cart, result);
    }

    @Test
    void creatingCartWhetherTheCartIsNull() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setCart(null);

        Cart cart = cartService.createCart(user);
        assertEquals(cart, user.getCart());
    }

    @Test
    void gettingExceptionCartNotExistsWhenRemovingItems() {
        User user = new User();
        user.setId(UUID.randomUUID());
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(CartNotExisting.class, () -> cartService.removeItemFromCart(user.getId(), UUID.randomUUID()));
    }

    @Test
    void gettingExceptionThatProductIsNotFound() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Product product1 = new Product();
        Product product2 = new Product();
        product1.setId(UUID.randomUUID());
        product2.setId(UUID.randomUUID());

        CartItem cartitem1 = CartItem.builder()
                .product(product1)
                .build();
        CartItem cartitem2 = CartItem.builder()
                .product(product2)
                .build();

        Cart cart = Cart.builder()
                .cartItems(List.of(cartitem1, cartitem2))
                .user(user)
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        assertThrows(ProductNotfoundException.class, () -> cartService.removeItemFromCart(user.getId(), UUID.randomUUID()));

    }

    @Test
    void removingCartItemFromCartCorrectly() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Product product1 = new Product();
        Product product2 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(230));
        product2.setId(UUID.randomUUID());
        product2.setPrice(BigDecimal.valueOf(680));

        CartItem cartitem1 = CartItem.builder()
                .product(product1)
                .build();
        CartItem cartitem2 = CartItem.builder()
                .product(product2)
                .build();

        Cart cart = Cart.builder()
                .cartItems(new ArrayList<>(List.of(cartitem1, cartitem2)))
                .user(user)
                .build();

        user.setCart(cart);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart(user.getId(), product1.getId());

        assertEquals(1 , user.getCart().getCartItems().size());
        assertEquals(user.getCart().getId(), cart.getId());
    }

    @Test
    void gettingExceptionThatCartNotExistingWhenUpdatingQuantity() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(CartNotExisting.class, () -> cartService.updateQuantity(user.getId(), UUID.randomUUID(), 2));

    }

    @Test
    void gettingExceptionThatProductNotFoundWhenUpdatingQuantity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        CartItem cartItem = CartItem.builder()
                .product(product1)
                .build();

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        assertThrows(ProductNotfoundException.class, () -> cartService.updateQuantity(user.getId(), UUID.randomUUID(), 2));
    }
    @Test
    void returningCorrectCartItemWhenUpdatingQuantity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Product product1 = new Product();
        product1.setPrice(BigDecimal.valueOf(230));
        product1.setId(UUID.randomUUID());
        CartItem cartItem = CartItem.builder()
                .product(product1)
                .build();

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));
        user.setCart(cart);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        cartService.updateQuantity(user.getId(), product1.getId(), 2);

        assertEquals(2 , user.getCart().getCartItems().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(460), user.getCart().getTotalPrice());
    }

    @Test
    void deletingCartItemFromCartWhenUpdatingQuantityLessThanZero() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(230));
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setPrice(BigDecimal.valueOf(680));
        CartItem cartItem1 = CartItem.builder()
                .product(product1)
                .build();
        CartItem cartItem2 = CartItem.builder()
                .product(product2)
                .build();
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>(List.of(cartItem1, cartItem2)));
        user.setCart(cart);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        cartService.updateQuantity(user.getId(), product1.getId(), -2);

        assertEquals(1 , user.getCart().getCartItems().size());

    }

    @Test
    void correctlyClearingCart() {
        User user = new User();
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setPrice(BigDecimal.valueOf(230));
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setPrice(BigDecimal.valueOf(680));
        CartItem cartItem1 = CartItem.builder()
                .product(product1)
                .build();
        CartItem cartItem2 = CartItem.builder()
                .product(product2)
                .build();
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>(List.of(cartItem1, cartItem2)));
        user.setCart(cart);

        cartService.clearCart(user);

        assertEquals(0 , user.getCart().getCartItems().size());
        assertEquals(BigDecimal.ZERO, user.getCart().getTotalPrice());
    }

}
