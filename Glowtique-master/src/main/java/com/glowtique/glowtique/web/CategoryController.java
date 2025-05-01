package com.glowtique.glowtique.web;

import com.glowtique.glowtique.category.model.CategoryType;
import com.glowtique.glowtique.category.service.CategoryService;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.service.ProductService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.wishlistitem.model.WishlistItem;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.glowtique.glowtique.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class CategoryController {
    private final ProductService productService;
    private final UserService userService;
    private final WishlistItemService wishlistItemService;
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(ProductService productService, UserService userService, WishlistItemService wishlistItemService, CategoryService categoryService) {
        this.productService = productService;
        this.userService = userService;
        this.wishlistItemService = wishlistItemService;
        this.categoryService = categoryService;
    }



}
