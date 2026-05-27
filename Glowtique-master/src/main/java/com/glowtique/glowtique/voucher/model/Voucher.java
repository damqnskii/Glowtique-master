package com.glowtique.glowtique.voucher.model;

import com.glowtique.glowtique.cart.model.Cart;
import com.glowtique.glowtique.order.model.Order;
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

    private LocalDateTime appliedAt;

    private LocalDateTime createdAt;

    private VoucherType voucherType;

    private LocalDateTime expiryDate;

    @OneToOne
    private Cart cart;

    @OneToOne
    private Order order;

    private boolean isTerminated;

    private boolean isUsed;
}
