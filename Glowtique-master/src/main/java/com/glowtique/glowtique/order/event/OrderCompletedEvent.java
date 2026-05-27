package com.glowtique.glowtique.order.event;

import com.glowtique.glowtique.order.model.Order;

public class OrderCompletedEvent {
    private final Order order;

    public OrderCompletedEvent(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
