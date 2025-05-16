package com.glowtique.glowtique.voucher.model;

import com.glowtique.glowtique.cart.model.Cart;
import jakarta.persistence.*;
import lombok.*;
import com.glowtique.glowtique.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private User user;

    private BigDecimal percentageDiscount;

    private BigDecimal priceDiscount;

    private LocalDateTime usedAt;

    private LocalDateTime createdAt;

    @OneToOne
    private Cart cart;

    private boolean isUsed;
}
