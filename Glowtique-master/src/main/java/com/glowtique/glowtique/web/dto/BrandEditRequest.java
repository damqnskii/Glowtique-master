package com.glowtique.glowtique.web.dto;

import lombok.*;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandEditRequest {
    private UUID id;
    private String name;
    private String description;
    private String logo;
}
