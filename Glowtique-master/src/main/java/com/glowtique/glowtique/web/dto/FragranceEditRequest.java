package com.glowtique.glowtique.web.dto;

import com.glowtique.glowtique.product.model.FragranceType;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FragranceEditRequest {
    private UUID id;
    private String baseNotes;
    private String heartNotes;
    private String topNotes;

    private Set<FragranceType> types;

}
