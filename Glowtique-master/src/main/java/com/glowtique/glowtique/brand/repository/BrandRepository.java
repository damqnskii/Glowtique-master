package com.glowtique.glowtique.brand.repository;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    Optional<Brand> findBrandById(UUID id);
}
