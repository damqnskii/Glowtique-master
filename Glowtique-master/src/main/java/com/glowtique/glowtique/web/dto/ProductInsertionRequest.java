package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.category.model.CategoryType;
import com.glowtique.glowtique.product.model.ProductGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInsertionRequest {
    @NotBlank(message = "This field cannot be blank!")
    private String productName;

    @NotBlank(message = "This field cannot be blank!")
    private String description;

    @NotNull(message = "This field cannot be null!")
    private BigDecimal price;

    private LocalDateTime createdAt;

    private BigDecimal discountPrice;

    @NotBlank(message = "This field cannot be blank!")
    private String image;

    @NotNull(message = "This field cannot be null!")
    private ProductGender productGender;

    private int quantity;

    private LocalDateTime updatedAt;
    @NotBlank(message = "This field cannot be blank!")
    private String brandName;

    @NotNull(message = "Category is required!")
    private CategoryType type;

    @NotNull(message = "This field cannot be null!")
    private UUID fragranceId;

    @NotBlank(message = "This field cannot be blank!")
    private String smallDescription;

    @NotBlank(message = "This field cannot be blank!")
    private String ingredients;

    private int volume;
}
