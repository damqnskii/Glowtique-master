package com.glowtique.glowtique.web;

import com.glowtique.glowtique.category.model.Category;
import com.glowtique.glowtique.category.model.CategoryType;
import com.glowtique.glowtique.category.service.CategoryService;
import com.glowtique.glowtique.product.service.ProductService;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.CategoryEditRequest;
import com.glowtique.glowtique.web.dto.CategoryRequest;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/category")
    public ModelAndView getCategoryOperations() {
        return new ModelAndView("admin-category");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/category/creation")
    public ModelAndView getCategoryCreation() {
        ModelAndView modelAndView = new ModelAndView("category-creation");
        modelAndView.addObject("categoryRequest", new CategoryRequest());
        modelAndView.addObject("types", Arrays.asList(CategoryType.values()));

        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin-dashboard/category/creation/create")
    public ModelAndView createCategory(@Valid CategoryRequest categoryRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("category-creation");
        }
        categoryService.createCategory(categoryRequest);
        return new ModelAndView("admin-category");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/category-list")
    public ModelAndView getCategoryList() {
        ModelAndView modelAndView = new ModelAndView("category-list");
        modelAndView.addObject("categoryList", categoryService.getAllCategories());
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/admin-dashboard/category/delete/{id}")
    public ModelAndView deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return new ModelAndView("category-list");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/category/edit/{id}")
    public ModelAndView getCategoryEdit(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView("category-edit");
        Category category = categoryService.getCategoryById(id);
        modelAndView.addObject("category", category);
        modelAndView.addObject("categoryRequest", new CategoryEditRequest(category.getId(), category.getName(), category.getDescription(), category.getCategoryType()));
        modelAndView.addObject("categoryTypes", CategoryType.values());
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/admin-dashboard/category/edit/update")
    public ModelAndView updateCategory(@Valid @ModelAttribute CategoryEditRequest categoryEditRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("category-edit");
        }
        categoryService.updateCategory(categoryEditRequest, categoryEditRequest.getId());
        return new ModelAndView("admin-category");
    }
}
