package com.glowtique.glowtique.category.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.category.model.Category;
import com.glowtique.glowtique.category.repository.CategoryRepository;
import com.glowtique.glowtique.web.dto.CategoryEditRequest;
import com.glowtique.glowtique.web.dto.CategoryRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
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
        return categoryRepository.getCategoryById(id);
    }

    @Transactional
    public Category createCategory(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .categoryType(categoryRequest.getType())
                .build();
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
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
        return categoryRepository.save(category);
    }
}
