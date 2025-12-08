package com.rmerezha.service;

import com.rmerezha.domain.Order;
import com.rmerezha.exception.OrderNotFoundException;
import com.rmerezha.persistence.mapper.OrderEntityMapper;
import com.rmerezha.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'API')")
    public List<Order> findAllOrders() {
        var entities = orderRepository.findAll();
        return orderMapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'API')")
    public Order findOrderById(Long id) {
        var entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toDomain(entity);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'API')")
    public Order createOrder(Order order) {
        var entity = orderMapper.toEntity(order);
        var savedEntity = orderRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Order updateOrder(Long id, Order orderChanges) {
        Order existingOrder = findOrderById(id);

        if (orderChanges.getStatus() != null) {
            existingOrder.setStatus(orderChanges.getStatus());
        }
        if (orderChanges.getTotalPrice() != null) {
            existingOrder.setTotalPrice(orderChanges.getTotalPrice());
        }

        if (orderChanges.getItems() != null && !orderChanges.getItems().isEmpty()) {
            existingOrder.setItems(orderChanges.getItems());
        }

        var entity = orderMapper.toEntity(existingOrder);
        var savedEntity = orderRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteOrderById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
    }
}