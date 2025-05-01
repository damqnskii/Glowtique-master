package com.glowtique.glowtique.brand.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Brand getBrandById(UUID id) {
        return brandRepository.findBrandById(id).orElseThrow(() -> new RuntimeException("There is no brand with this id"));
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }


}
