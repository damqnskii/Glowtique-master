package com.glowtique.glowtique.product.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
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

}
