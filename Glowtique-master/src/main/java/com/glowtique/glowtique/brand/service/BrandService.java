package com.glowtique.glowtique.brand.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.web.dto.BrandEditRequest;
import com.glowtique.glowtique.web.dto.BrandRequest;
import jakarta.transaction.Transactional;
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

    public Brand getBrandByName(String name) {
        return brandRepository.findBrandByName(name).orElseThrow(() -> new RuntimeException("There is no brand with this name"));
    }
    @Transactional
    public Brand createBrand(BrandRequest request) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogo(request.getLogo());
        return brandRepository.save(brand);
    }

    @Transactional
    public Brand updateBrand(UUID brandId, BrandEditRequest request) {
        Brand brand = getBrandById(brandId);
        if (request.getName() != null) {
            brand.setName(request.getName());
        }
        if (request.getDescription() != null) {
            brand.setDescription(request.getDescription());
        }
        if (request.getLogo() != null) {
            brand.setLogo(request.getLogo());
        }
        return brandRepository.save(brand);
    }

    public void deleteBrand(UUID brandId) {
        brandRepository.deleteById(brandId);
    }

}
