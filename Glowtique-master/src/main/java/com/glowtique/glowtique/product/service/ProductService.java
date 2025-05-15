package com.glowtique.glowtique.product.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.brand.service.BrandService;
import com.glowtique.glowtique.category.service.CategoryService;
import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.model.ProductGender;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.web.dto.ProductInsertionRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final FragranceService fragranceService;


    @Autowired
    public ProductService(ProductRepository productRepository, BrandService brandService, CategoryService categoryService, FragranceService fragranceService) {
        this.productRepository = productRepository;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.fragranceService = fragranceService;
    }


    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> getAllProductsByBrandId(UUID brandId) {
        return productRepository.findAllByBrandId(brandId);
    }

    private boolean isProductExists(UUID id) {
        return productRepository.findById(id).isPresent();
    }


    public Page<Product> filterProducts(List<UUID> categories, List<String> genders,List<Integer> volumes, Double minPrice, Double maxPrice, Pageable pageable) {
        List<ProductGender> genderEnums = null;
        if (genders != null && !genders.isEmpty()) {
            genderEnums = genders.stream()
                    .map(String::toUpperCase)
                    .map(ProductGender::valueOf)
                    .toList();
        }
        return productRepository.filterProducts(categories, genderEnums, volumes, minPrice, maxPrice, pageable);
    }

    public Page<Product> filterBrandProducts(List<UUID> categories, List<String> genders, List<Integer> volume, Double minPrice, Double maxPrice, Brand brand , Pageable pageable) {
        List<ProductGender> genderEnums = null;
        if (genders != null && !genders.isEmpty()) {
            genderEnums = genders.stream()
                    .map(String::toUpperCase)
                    .map(ProductGender::valueOf)
                    .toList();
        }
        return productRepository.filterBrandProducts(categories, genderEnums, volume, minPrice, maxPrice, brand, pageable);
    }

    public List<Product> getAllProductsByCategoryAndBrand(UUID brandId, List<UUID> categories) {
        return productRepository.findProductsByBrandIdAndCategoryIdIn(brandId, categories);
    }


    public List<Product> findTop10ByNameContainingIgnoreCase(String name) {
        return productRepository.findTop10ByNameContainingIgnoreCase(name);
    }
    public List<Integer> findAllVolumes() {
        return productRepository.findAllDistinctProductVolume();
    }



    @Transactional
    public Product createProduct(ProductInsertionRequest request) {
        Product product = new Product();
        product.setDescription(request.getDescription());
        product.setName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setImage(request.getImage());
        product.setProductGender(request.getProductGender());
        product.setQuantity(request.getQuantity());
        product.setSmallDescription(request.getSmallDescription());
        product.setIngredients(request.getIngredients());
        product.setVolume(request.getVolume());
        product.setBrand(brandService.getBrandById(request.getBrandId()));
        product.setCategory(categoryService.getCategoryById(request.getCategoryId()));
        product.setFragrance(fragranceService.getFragranceById(request.getFragranceId()));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        return product;
    }

    public BigDecimal maxPrice(Page<Product> products) {
        return productRepository.findTheExpensiveProductPrice(products);
    }
}
