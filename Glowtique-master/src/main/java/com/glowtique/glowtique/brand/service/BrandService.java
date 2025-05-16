package com.glowtique.glowtique.brand.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.exception.BrandWithThisNameNotFound;
import com.glowtique.glowtique.web.dto.BrandEditRequest;
import com.glowtique.glowtique.web.dto.BrandRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
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
        return brandRepository.findAllByOrderByNameAsc();
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

        log.info("Brand created: {} ", brand);
        log.info("Brand name: {}", brand.getName());
        log.info("Brand was created at: {}", brand.getLogo());
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

        log.info("Brand updated: {} ", brand);
        log.info("Brand was updated at: {}", LocalDateTime.now());
        return brandRepository.save(brand);
    }

    public void deleteBrand(UUID brandId) {
        brandRepository.deleteById(brandId);
        log.info("Brand deleted: {}", brandId);
        log.info("Brand was deleted at: {}", LocalDateTime.now());
    }

    public Brand getBrandByBrandName(String brandName) {
        return brandRepository.findBrandByName(brandName).orElseThrow(() -> new BrandWithThisNameNotFound("There is no brand with this name"));
    }

}
