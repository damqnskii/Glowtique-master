package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.category.model.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    @NotNull(message = "Category's name cannot be null !")
    @NotBlank(message = "Category's name cannot be blank !")
    private String name;

    @Size(message = "Category's description can be max 750 characters", max = 750)
    @NotNull(message = "Category's description cannot be null !")
    @NotBlank(message = "Category's description cannot be blank !")
    private String description;

    @NotNull(message = "Category's type cannot be null!")
    private CategoryType type;
}
