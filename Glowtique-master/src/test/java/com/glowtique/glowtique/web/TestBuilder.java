package com.glowtique.glowtique.web;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.cart.model.CartItem;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.user.model.Country;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.user.model.UserGender;
import com.glowtique.glowtique.user.model.UserRole;
import com.glowtique.glowtique.wishlistitem.model.WishlistItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class TestBuilder {
    private Order order;
    private Cart cart;
    private User user;
    private CartItem cartItem;


    public static User aRandomUser() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .firstName("damian")
                .lastName("apostolov")
                .password("123456")
                .gender(UserGender.MALE)
                .birthday(LocalDate.now().minusYears(20))
                .phoneNumber("0898946872")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .factureAddress("Dobrujda 11")
                .shippingAddress("Dobrujda 11")
                .country(Country.BULGARIA)
                .orders(Set.of(order))
                .cart(new Cart())
                .wishlistItems(Set.of(new WishlistItem()))
                .build();


        order = Order.builder()
                .id(UUID.randomUUID())
                .user(user)
                .totalPrice(BigDecimal.ZERO)
                .trackingNumber(UUID.randomUUID().toString())
                .orderDate(LocalDateTime.now())
                .shippingAddress("Dobrujda 11")
                .orderPhoneNumber("0898946872")
                .postalCode("9300")
                .build();


        return user;

    }
}
