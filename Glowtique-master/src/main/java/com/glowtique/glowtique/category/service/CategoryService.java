package com.glowtique.glowtique.category.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.category.model.Category;
import com.glowtique.glowtique.category.model.CategoryType;
import com.glowtique.glowtique.category.repository.CategoryRepository;
import com.glowtique.glowtique.exception.CategoryWithThisNotFound;
import com.glowtique.glowtique.web.dto.CategoryEditRequest;
import com.glowtique.glowtique.web.dto.CategoryRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    public Category getCategoryById(UUID id) {
        return categoryRepository.getCategoryById(id).orElseThrow(() -> new CategoryWithThisNotFound("Category with this name is not found!"));
    }

    @Transactional
    public Category createCategory(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .categoryType(categoryRequest.getType())
                .build();
        log.info("Create category: {}", category);
        log.info("Category was created at {}", LocalDateTime.now());
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
        log.info("Deleted Category with id {}", id);
        log.info("Category was deleted at {}", LocalDateTime.now());
    }

    @Transactional
    public Category updateCategory(CategoryEditRequest request, UUID id) {
        Category category = getCategoryById(id);
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            category.setCategoryType(request.getType());
        }

        log.info("Updating category with id {}", id);
        log.info("Updating category with name {}", category.getName());
        log.info("Updating category at {}", LocalDateTime.now());
        return categoryRepository.save(category);
    }

    public Category getCategoryByCategoryType(CategoryType type) {
        return categoryRepository.getCategoryByCategoryType(type).orElseThrow(() -> new CategoryWithThisNotFound("Category with this name is not found!"));
    }
}
