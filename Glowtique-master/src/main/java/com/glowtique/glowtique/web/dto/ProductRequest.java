package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.product.model.Product;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private UUID id;
    private String name;
    private String image;
    private BigDecimal price;
    private int volume;
    private String description;
    private int quantity;


}
