package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.category.model.CategoryType;
import lombok.*;

import java.util.UUID;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEditRequest {
    private UUID id;
    private String name;
    private String description;
    private CategoryType type;
}
