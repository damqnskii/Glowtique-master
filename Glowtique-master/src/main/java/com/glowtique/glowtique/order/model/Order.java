package com.glowtique.glowtique.order.model;

import com.glowtique.glowtique.payment.model.Payment;
import com.glowtique.glowtique.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String orderPhoneNumber;

    private String officeAddress;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String town;

    @Enumerated(EnumType.STRING)
    private OrderMethod orderMethod;

    @Column(length = 750)
    private String description;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
}
