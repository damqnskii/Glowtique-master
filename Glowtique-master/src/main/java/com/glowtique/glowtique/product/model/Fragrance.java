package com.glowtique.glowtique.product.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fragrance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String topNotes;
    private String heartNotes;
    private String baseNotes;

    @ElementCollection(targetClass = FragranceType.class)
    @CollectionTable(
            name = "fragrance_types",
            joinColumns = @JoinColumn(name = "fragrance_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Set<FragranceType> type = new HashSet<>();

    @Transient
    public String getTypeValuesCommaSeparated() {
        return type.stream()
                .map(FragranceType::getValue)
                .sorted()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }


}
