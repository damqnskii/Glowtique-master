package com.glowtique.glowtique.product.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.brand.service.BrandService;
import com.glowtique.glowtique.category.service.CategoryService;
import com.glowtique.glowtique.exception.ProductNotfoundException;
import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.model.ProductGender;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.web.dto.ProductEditRequest;
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
        return productRepository.findById(id).orElseThrow(() -> new ProductNotfoundException("Product not found"));
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
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
        product.setBrand(brandService.getBrandByBrandName(request.getBrandName()));
        product.setCategory(categoryService.getCategoryByCategoryType(request.getType()));
        product.setFragrance(fragranceService.getFragranceById(request.getFragranceId()));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        log.info("Created product: {}", product);
        log.info("Created at: {}", product.getCreatedAt());
        return product;
    }

    public BigDecimal maxPrice(Page<Product> products) {
        return productRepository.findTheExpensiveProductPrice(products);
    }

    @Transactional
    public Product updateProduct(ProductEditRequest request, UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotfoundException("Product wasn't found"));
        product.setName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setImage(request.getImage());
        product.setProductGender(request.getProductGender());
        product.setQuantity(request.getQuantity());
        product.setUpdatedAt(LocalDateTime.now());
        product.setBrand(brandService.getBrandByBrandName(request.getBrandName()));
        System.out.println("brand is correct");
        product.setCategory(categoryService.getCategoryByCategoryType(request.getType()));
        System.out.println("category is correct");
        product.setFragrance(fragranceService.getFragranceById(request.getFragranceId()));
        System.out.println("fragrance is correct");
        product.setSmallDescription(request.getSmallDescription());
        product.setIngredients(request.getIngredients());
        product.setVolume(request.getVolume());

        log.info("Updating product with ID: {}", id);
        log.info("Updating product with Name: {}", request.getProductName());

        return productRepository.save(product);
    }
}
