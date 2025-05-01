package com.glowtique.glowtique.cart.repository;

import com.glowtique.glowtique.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import com.glowtique.glowtique.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserId(UUID userId);
    Optional<Cart> findByUser(User user);
}
