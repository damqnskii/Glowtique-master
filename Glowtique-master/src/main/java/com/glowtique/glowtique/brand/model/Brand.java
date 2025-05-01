package com.glowtique.glowtique.brand.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String logo;

    @Column(nullable = false)
    private String name;

    @Column(length = 750)
    private String description;

}
