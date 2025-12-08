package com.rmerezha.service;

import com.rmerezha.PostgresIT;
import com.rmerezha.domain.*;
import com.rmerezha.exception.OrderNotFoundException;
import com.rmerezha.persistence.entity.OrderEntity;
import com.rmerezha.repository.OrderRepository;
import com.rmerezha.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@ActiveProfiles("no-auth")
@WithMockUser(username = "admin", roles = {"ADMIN"})
class OrderServiceIT extends PostgresIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("createOrder: should create order when valid")
    void testCreateOrder_WhenValid_ReturnsCreatedOrder() {
        Order newOrder = createDummyOrder();

        Order created = orderService.createOrder(newOrder);

        assertNotNull(created.getId());
        assertEquals(OrderStatus.NEW, created.getStatus());
        assertEquals(1, orderRepository.count());
    }

    @Test
    @DisplayName("findOrderById: should return order when exists")
    void testFindOrderById_WhenExists_ReturnsOrder() {
        Order order = orderService.createOrder(createDummyOrder());

        Order found = orderService.findOrderById(order.getId());

        assertNotNull(found);
        assertEquals(order.getId(), found.getId());
        assertEquals(OrderStatus.NEW, found.getStatus());
    }

    @Test
    @DisplayName("findOrderById: should throw exception when order not found")
    void testFindOrderById_WhenNotFound_ThrowsException() {
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.findOrderById(999L);
        });
    }

    @Test
    @DisplayName("findAllOrders: should return list of all orders")
    void testFindAllOrders_WhenCalled_ReturnsList() {
        orderService.createOrder(createDummyOrder());
        orderService.createOrder(createDummyOrder());

        List<Order> orders = orderService.findAllOrders();

        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("updateOrder: should update status and price when valid")
    void testUpdateOrder_WhenValid_ReturnsUpdatedOrder() {
        Order original = orderService.createOrder(createDummyOrder());

        Order changes = new Order();
        changes.setStatus(OrderStatus.SHIPPED);
        changes.setTotalPrice(BigDecimal.valueOf(500.00));

        Order updated = orderService.updateOrder(original.getId(), changes);

        assertEquals(OrderStatus.SHIPPED, updated.getStatus());
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(updated.getTotalPrice()));

        OrderEntity inDb = orderRepository.findById(original.getId()).orElseThrow();
        assertEquals(OrderStatus.SHIPPED, inDb.getStatus());
    }

    @Test
    @DisplayName("updateOrder: should not update fields if they are null")
    void testUpdateOrder_WhenFieldsNull_DoesNotUpdate() {
        Order original = orderService.createOrder(createDummyOrder());

        Order changes = new Order();
        changes.setStatus(OrderStatus.COMPLETED);

        Order updated = orderService.updateOrder(original.getId(), changes);

        assertEquals(OrderStatus.COMPLETED, updated.getStatus());

        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(updated.getTotalPrice()));
    }

    @Test
    @DisplayName("deleteOrderById: should delete order from database")
    void testDeleteOrderById_WhenCalled_DeletesEntity() {
        Order order = orderService.createOrder(createDummyOrder());

        orderService.deleteOrderById(order.getId());

        assertFalse(orderRepository.existsById(order.getId()));
    }

    @Test
    @DisplayName("deleteOrderById: should throw exception if order does not exist")
    void testDeleteOrderById_WhenNotFound_ThrowsException() {
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.deleteOrderById(999L);
        });
    }

    private Order createDummyOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setTotalPrice(BigDecimal.valueOf(100.00));
        return order;
    }
}
