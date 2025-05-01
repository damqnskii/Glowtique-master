package com.glowtique.glowtique.wishlistitem.repository;

import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.wishlistitem.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, UUID> {
    boolean existsByUserAndProduct(User user, Product product);
    List<WishlistItem> findTop15ByUser(User user);
    void deleteWishlistItemByUserAndProduct(User user, Product product);
    WishlistItem findWishListItemByProduct(Product product);
    List<WishlistItem> findAllByUser(User user);

}
