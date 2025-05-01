package com.glowtique.glowtique.order.repository;

import com.glowtique.glowtique.order.model.Order;
import com.glowtique.glowtique.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.orderDate DESC LIMIT 1")
    Optional<Order> getLastOrderByUserId(@Param("userId")UUID userId);

    List<Order> getAllOrderByOrderStatusAndOrderDateBefore(OrderStatus orderStatus, LocalDateTime orderDate);
}
