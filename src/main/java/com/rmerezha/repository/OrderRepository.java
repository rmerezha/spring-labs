package com.rmerezha.repository;

import com.rmerezha.persistence.entity.OrderEntity;
import com.rmerezha.repository.projection.SalesReportProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("""
        SELECT 
            p.name as productName, 
            p.category as category, 
            p.price as productPrice,
            i.quantity as quantitySold,
            o.createdAt as orderDate,
            o.status as orderStatus
        FROM OrderEntity o
        JOIN o.items i       
        JOIN i.product p     
        WHERE o.status = 'COMPLETED' 
        ORDER BY i.quantity DESC
    """)
    List<SalesReportProjection> findTopSellingItems();
}