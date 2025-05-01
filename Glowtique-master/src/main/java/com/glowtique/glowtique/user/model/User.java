package com.glowtique.glowtique.user.model;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.wishlistitem.model.WishlistItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    private LocalDate birthday;

    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String town;

    private String street;

    private boolean isActive;

    private int loyaltyPoints;

    private String shippingAddress;

    private String factureAddress;

    @Enumerated(EnumType.STRING)
    private Country country;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<WishlistItem> wishlistItems = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;
}
