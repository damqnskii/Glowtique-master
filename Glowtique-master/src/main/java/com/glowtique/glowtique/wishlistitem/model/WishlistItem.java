package com.glowtique.glowtique.wishlistitem.model;

import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;

    @Column(nullable = false)
    private boolean isLiked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistItem that = (WishlistItem) o;
        return Objects.equals(user, that.user) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }
}
