package com.glowtique.glowtique.web;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.service.BrandService;
import com.glowtique.glowtique.category.service.CategoryService;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.model.ProductGender;
import com.glowtique.glowtique.product.service.FragranceService;
import com.glowtique.glowtique.product.service.ProductService;
import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.web.dto.ProductInsertionRequest;
import com.glowtique.glowtique.wishlistitem.model.WishlistItem;
import com.glowtique.glowtique.wishlistitem.service.WishlistItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;


@RestController
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final WishlistItemService wishlistItemService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final FragranceService fragranceService;

    @Autowired
    public ProductController(ProductService productService, UserService userService, WishlistItemService wishlistItemService, BrandService brandService, CategoryService categoryService, FragranceService fragranceService) {
        this.productService = productService;
        this.userService = userService;
        this.wishlistItemService = wishlistItemService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.fragranceService = fragranceService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/product")
    public ModelAndView getProductOperations() {
        return new ModelAndView("admin-product");
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/product/creation")
    public ModelAndView getProductInsertion() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("product-creation");
        modelAndView.addObject("productInsertionRequest", new ProductInsertionRequest());
        modelAndView.addObject("productGender", ProductGender.values());

        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin-dashboard/product/creation/create")
    public ModelAndView createProduct(@Valid ProductInsertionRequest productInsertionRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("product-creation");
        }

        productService.createProduct(productInsertionRequest);
        return new ModelAndView("admin-product");

    }



    @GetMapping("/products")
    public ModelAndView getSpecifiedCategory(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                             @RequestParam(required = false) List<UUID> categories,
                                             @RequestParam(required = false) List<String> genders,
                                             @RequestParam(required = false) List<Integer> volume,
                                             @RequestParam(required = false) Double minPrice,
                                             @RequestParam(required = false) Double maxPrice,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "15") int size) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());
        List<WishlistItem> wishListed = wishlistItemService.wishListedItems(user);
        List<UUID> wishListIds = wishListed.stream().map(w -> w.getProduct().getId()).toList();

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> filteredProducts = productService.filterProducts(categories, genders, volume, minPrice, maxPrice, pageable);

        ModelAndView modelAndView = new ModelAndView("products");
        modelAndView.addObject("products", filteredProducts.getContent());
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", filteredProducts.getTotalPages());
        modelAndView.addObject("wishListed", wishListIds);
        modelAndView.addObject("wishlistItems", wishListed);
        modelAndView.addObject("categoryList", categoryService.getAllCategories());
        modelAndView.addObject("selectedCategoryIds", categories);
        modelAndView.addObject("selectedGender", genders);
        modelAndView.addObject("minPrice", minPrice);
        modelAndView.addObject("maxPrice", maxPrice);
        modelAndView.addObject("selectedVolume", volume);
        modelAndView.addObject("allVolumes", productService.findAllVolumes());


        return modelAndView;
    }

    @GetMapping("/product/{id}")
    public ModelAndView getProductDetails(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                          @PathVariable UUID id) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }

        User user = userService.getUserById(authenticationMetadata.getUserId());

        Product product = productService.getProductById(id);
        List<Product> productsWithSameName = productService.getProductsByName(product.getName());

        List<WishlistItem> wishListed = wishlistItemService.wishListedItems(user);
        List<UUID> wishListIds = wishListed.stream().map(w -> w.getProduct().getId()).toList();


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("product-details");
        modelAndView.addObject("product", product);
        modelAndView.addObject("relatedProducts", productsWithSameName);
        modelAndView.addObject("user", user);
        modelAndView.addObject("fragrance", product.getFragrance());
        modelAndView.addObject("wishListed", wishListIds);

        return modelAndView;
    }



    @GetMapping("/product-brand/{brandId}")
    public ModelAndView getSpecifiedBrandWithCategories(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                                        @PathVariable UUID brandId,
                                                        @RequestParam(required = false) List<UUID> categories,
                                                        @RequestParam(required = false) List<String> genders,
                                                        @RequestParam(required = false) List<Integer> volume,
                                                        @RequestParam(required = false) Double minPrice,
                                                        @RequestParam(required = false) Double maxPrice,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "15") int size) {
        if (authenticationMetadata == null) {
            return new ModelAndView("redirect:/login");
        }
        List<WishlistItem> wishlistItems = wishlistItemService.wishListedItems(userService.getUserById(authenticationMetadata.getUserId()));
        List<UUID> wishListedIds = wishlistItems.stream().map(w -> w.getProduct().getId()).toList();
        Brand brand = brandService.getBrandById(brandId);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> filteredProducts = productService.filterBrandProducts(categories, genders, volume, minPrice, maxPrice, brand, pageable);


        ModelAndView modelAndView = new ModelAndView("product-brand");
        modelAndView.addObject("products", filteredProducts);
        modelAndView.addObject("wishListed", wishListedIds);
        modelAndView.addObject("wishlistItems", wishlistItems);
        modelAndView.addObject("brand", brand);
        modelAndView.addObject("categoryList", categoryService.getAllCategories());
        modelAndView.addObject("minPrice", minPrice);
        modelAndView.addObject("maxPrice", maxPrice);
        modelAndView.addObject("selectedGender", genders);
        modelAndView.addObject("selectedCategoryIds", categories);
        modelAndView.addObject("selectedVolume", volume);
        modelAndView.addObject("allVolumes", productService.findAllVolumes());

        return modelAndView;

    }
    @GetMapping("/products/suggest")
    @ResponseBody
    public List<Map<String, Object>> productSuggestions(@RequestParam String query) {
        List<Product> results = productService.findTop10ByNameContainingIgnoreCase(query);

        return results.stream()
                .map(p -> { Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("name", p.getName());
                map.put("volume", p.getVolume());
                map.put("image", p.getImage());
                return map;
                }).collect(Collectors.toList());
    }

}
