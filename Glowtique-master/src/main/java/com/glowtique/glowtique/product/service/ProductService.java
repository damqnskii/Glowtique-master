package com.glowtique.glowtique.product.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.model.ProductGender;
import com.glowtique.glowtique.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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

}
