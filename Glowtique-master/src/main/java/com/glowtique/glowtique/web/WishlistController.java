package com.glowtique.glowtique.web;

import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.ProductRequest;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.glowtique.glowtique.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class WishlistController {
    private final WishlistItemService wishlistItemService;
    private final UserService userService;

    @Autowired
    public WishlistController(WishlistItemService wishlistItemService, UserService userService) {
        this.wishlistItemService = wishlistItemService;
        this.userService = userService;
    }
    @GetMapping("/wishlist/items")
    public ResponseEntity<List<ProductRequest>> getWishlistItems(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<ProductRequest> wishlistItems = wishlistItemService.getWishListItemsAsProductRequest(user);
        return ResponseEntity.ok(wishlistItems);
    }


    @PostMapping("/{context}/add/{productId}")
    public ResponseEntity<Map<String, Object>> addToWishlist(@PathVariable String context,
                                                             @PathVariable UUID productId,
                                                             @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (authenticationMetadata == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User is not logged in!"));
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());
        boolean added = wishlistItemService.addToWishlist(productId, user);
        Map<String, Object> response = Map.of(
                "added", added,
                "message", added ? "The product is added successfully" : "The product is removed successfully!"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{context}/remove/{productId}")
    public ResponseEntity<Map<String, Object>>  removeFromWishlist(@PathVariable String context,
            @PathVariable UUID productId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User is not logged in!"));
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());
        wishlistItemService.removeFromWishlist(productId, user);
        Map<String, Object> response = new HashMap<>();
        response.put("added", false);
        response.put("message", "Продуктът е премахнат от любими!");

        return ResponseEntity.ok(response);
    }
}
