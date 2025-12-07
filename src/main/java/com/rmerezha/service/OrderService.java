package com.rmerezha.service;

import com.rmerezha.domain.Order;
import java.util.List;

public interface OrderService {
    List<Order> findAllOrders();
    Order findOrderById(Long id);
    Order createOrder(Order order);
    Order updateOrder(Long id, Order orderChanges);
    void deleteOrderById(Long id);
}