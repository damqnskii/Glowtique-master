package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.product.model.ProductGender;
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
    private String productName;
    private String description;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private BigDecimal discountPrice;
    private String image;
    private ProductGender productGender;
    private int quantity;
    private LocalDateTime updatedAt;
    private UUID brandId;
    private UUID categoryId;
    private UUID fragranceId;
    private String smallDescription;
    private String ingredients;
    private int volume;
}
