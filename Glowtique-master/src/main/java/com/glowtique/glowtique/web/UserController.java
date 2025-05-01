package com.glowtique.glowtique.web;

import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.AdminRequest;
import com.glowtique.glowtique.web.dto.EditProfileRequest;
import com.glowtique.glowtique.web.dto.ProductRequest;
import com.glowtique.glowtique.web.mapper.DtoMapper;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.glowtique.glowtique.user.model.User;


import java.util.List;
import java.util.UUID;

@Controller
public class UserController {
    private final UserService userService;
    private final WishlistItemService wishlistItemService;

    @Autowired
    public UserController(UserService userService, WishlistItemService wishlistItemService) {
        this.userService = userService;
        this.wishlistItemService = wishlistItemService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard")
    public ModelAndView getAdminDashboard() {
        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin-dashboard");
        modelAndView.addObject("users", users);
        modelAndView.addObject("adminRequest", new AdminRequest());
        return modelAndView;
    }

    @PutMapping("/admin-dashboard/edit/{id}")
    public String editRole(@PathVariable UUID id, @ModelAttribute AdminRequest adminRequest) {
        userService.updateRole(adminRequest, id);
        return "redirect:/admin-dashboard";
    }

    @GetMapping("/profile")
    public ModelAndView getProfileMenu(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/edit-profile")
    public ModelAndView getEditProfileForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editProfileRequest", DtoMapper.mapUserToEditRequest(user));
        return modelAndView;
    }

    @PutMapping("/edit-profile")
    public ModelAndView updateUser(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, @Valid EditProfileRequest editProfileRequest, BindingResult bindingResult) {
        UUID userId = authenticationMetadata.getUserId();
        User user = userService.getUserById(authenticationMetadata.getUserId());

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editProfileRequest", editProfileRequest);
            return modelAndView;
        }
        userService.updateUser(userId, editProfileRequest);

        return new ModelAndView("redirect:/profile");

    }
    @GetMapping("/wishlist")
    public ModelAndView getWishlist(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                    @RequestParam (defaultValue = "0") int pages,
                                    @RequestParam (defaultValue = "12") int size) {

        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());


        Pageable pageable = PageRequest.of(pages, size);
        Page<ProductRequest> wishlistItems = wishlistItemService.getPagesWithWishlistItems(user, pageable);

        return new ModelAndView("wishlist")
                .addObject("totalPages", wishlistItems.getTotalPages())
                .addObject("currentPage", pages)
                .addObject("wishlistItems", wishlistItems.getContent())
                .addObject("user", user);
    }

    @GetMapping("/loyalty-points")
    public ModelAndView getLoyaltyPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("loyalty-points");
    }

}
