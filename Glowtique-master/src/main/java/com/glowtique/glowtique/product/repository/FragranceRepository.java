package com.glowtique.glowtique.product.repository;

import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.FragranceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FragranceRepository extends JpaRepository<Fragrance, UUID> {
    Fragrance getFragranceById(UUID id);
}
