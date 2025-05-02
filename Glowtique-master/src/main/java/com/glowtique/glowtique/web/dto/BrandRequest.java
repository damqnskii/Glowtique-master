package com.glowtique.glowtique.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BrandRequest {
    @NotNull(message = "This field cannot be null!")
    @NotBlank(message = "This field cannot be blank!")
    private String logo;
    @NotNull(message = "This field cannot be null!")
    @NotBlank(message = "This field cannot be blank!")
    private String name;
    @NotNull(message = "This field cannot be null!")
    @NotBlank(message = "This field cannot be blank!")
    @Size(max = 750)
    private String description;
}
