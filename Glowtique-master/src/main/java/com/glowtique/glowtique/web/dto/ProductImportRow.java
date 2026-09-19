package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.category.model.CategoryType;
import com.glowtique.glowtique.product.model.FragranceType;
import com.glowtique.glowtique.product.model.ProductGender;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImportRow {
    private String name;
    private String brandName;
    private CategoryType categoryType;
    private ProductGender productGender;

    private BigDecimal price;
    private BigDecimal discountPrice;

    private Integer quantity;
    private Integer volume;

    private String image;
    private String description;
    private String smallDescription;
    private String ingredients;

    private String topNotes;
    private String heartNotes;
    private String baseNotes;

    private Set<FragranceType> fragranceTypes;

}
