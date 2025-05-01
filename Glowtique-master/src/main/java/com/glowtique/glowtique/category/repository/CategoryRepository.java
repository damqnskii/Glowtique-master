package com.glowtique.glowtique.category.repository;

import com.glowtique.glowtique.category.model.Category;
import com.glowtique.glowtique.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAll();
}
