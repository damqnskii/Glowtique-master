package com.glowtique.glowtique.payment.model;

import com.glowtique.glowtique.order.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
