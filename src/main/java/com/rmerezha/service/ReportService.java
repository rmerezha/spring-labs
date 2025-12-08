package com.rmerezha.service;

import com.rmerezha.repository.OrderRepository;
import com.rmerezha.repository.projection.SalesReportProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<SalesReportProjection> getTopSales() {
        return orderRepository.findTopSellingItems();
    }
}