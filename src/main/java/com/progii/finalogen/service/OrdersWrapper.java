package com.progii.finalogen.service;

import com.progii.finalogen.domain.Order;
import java.util.List;

public class OrdersWrapper {

    private List<Order> ordenes;

    public List<Order> getOrders() {
        return ordenes;
    }

    public void setOrders(List<Order> ordenes) {
        this.ordenes = ordenes;
    }
}
