package com.glowtique.glowtique.wishlistitem.service;

import com.glowtique.glowtique.exception.ProductNotfoundException;
import com.glowtique.glowtique.exception.UnauthorizedException;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.user.repository.UserRepository;
import com.glowtique.glowtique.web.dto.ProductRequest;
import com.glowtique.glowtique.wishlistitem.model.WishlistItem;
import com.glowtique.glowtique.wishlistitem.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.glowtique.glowtique.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WishlistItemService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public WishlistItemService(WishlistRepository wishlistRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean addToWishlist(UUID productId, User user) {
        if (user.getId() == null) {
            throw new UnauthorizedException("Трябва да влезете в профила си!");
        }
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotfoundException("Продуктът не е намерен!");
        }
        Product product = optionalProduct.get();
        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            wishlistRepository.deleteWishlistItemByUserAndProduct(user, product);
            return false;
        }
        WishlistItem wishlistItem = WishlistItem.builder()
                .product(product)
                .user(user)
                .isLiked(true)
                .build();
        wishlistRepository.save(wishlistItem);
        user.getWishlistItems().add(wishlistItem);
        userRepository.save(user);
        return true;
    }

    public List<ProductRequest> getWishListItemsAsProductRequest(User user) {
        List<WishlistItem> wishlistItems = wishlistRepository.findAllByUser(user);

        return wishlistItems.stream().map(w -> ProductRequest.builder()
                .id(w.getProduct().getId())
                .name(w.getProduct().getName())
                .image(w.getProduct().getImage())
                .price(w.getProduct().getPrice())
                .volume(w.getProduct().getVolume())
                .quantity(w.getProduct().getQuantity())
                .description(w.getProduct().getDescription())
                .build()).toList();
    }

    @Transactional
    public boolean removeFromWishlist(UUID productId, User user) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotfoundException("Продуктът не е намерен!");
        }
        Product product = optionalProduct.get();
        WishlistItem wishlistItem = wishlistRepository.findWishListItemByProduct(product);
        user.getWishlistItems().remove(wishlistItem);
        userRepository.save(user);
        wishlistRepository.deleteWishlistItemByUserAndProduct(user, product);

        return true;
    }

    public List<WishlistItem> wishListedItems (User user) {
        return wishlistRepository.findTop15ByUser(user);
    }

    public Page<ProductRequest> getPagesWithWishlistItems(User user, Pageable pageable) {
        List<ProductRequest> wishlistItems = getWishListItemsAsProductRequest(user);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), wishlistItems.size());

        List<ProductRequest> sublist = wishlistItems.subList(start, end);
        return new PageImpl<>(sublist, pageable, wishlistItems.size());
    }

}
