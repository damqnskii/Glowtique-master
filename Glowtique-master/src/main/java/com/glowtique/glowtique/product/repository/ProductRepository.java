package com.glowtique.glowtique.product.repository;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.category.model.Category;
import com.glowtique.glowtique.category.model.CategoryType;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.model.ProductGender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("""
    SELECT p FROM Product p
    WHERE (:#{#genders == null || #genders.isEmpty()} = true OR p.productGender IN :genders)
      AND (:#{#categories == null || #categories.isEmpty()} = true OR p.category.id IN :categories)
      AND (:#{#volume == null || #volume.isEmpty()} = true OR p.volume IN :volume)
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
""")
    Page<Product> filterProducts(
            @Param("categories") List<UUID> categories,
            @Param("genders") List<ProductGender> genders,
            @Param("volume") List<Integer> volume,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);
    @NativeQuery("""
        SELECT DISTINCT volume FROM product
        ORDER BY volume;""")
    List<Integer> findAllDistinctProductVolume();
    @Query("""
    SELECT p FROM Product p
    WHERE p.brand = :brand
        AND (:#{#genders == null || #genders.isEmpty()} = true OR p.productGender IN :genders)
        AND (:#{#categories == null || #categories.isEmpty()} = true OR p.category.id IN :categories)
        AND (:#{#volume == null || #volume.isEmpty()} = true OR p.volume IN :volume)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
""")
    Page<Product> filterBrandProducts(@Param("categories") List<UUID> categories,
                                      @Param("genders") List<ProductGender> genders,
                                      @Param("volume") List<Integer> volume,
                                      @Param("minPrice") Double minPrice,
                                      @Param("maxPrice") Double maxPrice,
                                      Brand brand,
                                      Pageable pageable);
    Optional<Product> findById(UUID id);
    List<Product> findByName(String name);
    List<Product> findAllByBrandId(UUID id);
    List<Product> findProductsByBrandIdAndCategoryIdIn(UUID brandId, List<UUID> categories);
    List<Product> findTop10ByNameContainingIgnoreCase(String name);
    Page<Product> findAll(Pageable pageable);
}
