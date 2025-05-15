package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.product.model.FragranceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FragranceRequest {
    @NotNull(message = "This field cannot be null!")
    @NotBlank(message = "This field cannot be blank!")
    private String topNotes;
    @NotNull(message = "This field cannot be null!")
    @NotBlank(message = "This field cannot be blank!")
    private String heartNotes;
    @NotNull(message = "This field cannot be null!")
    @NotBlank(message = "This field cannot be blank!")
    private String baseNotes;

    private Set<FragranceType> types;
}
