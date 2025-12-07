package com.rmerezha.web;

import com.rmerezha.domain.Product;
import com.rmerezha.dto.ProductListDto;
import com.rmerezha.repository.projection.SalesReportProjection;
import com.rmerezha.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<List<SalesReportProjection>> getTopSales() {
        List<SalesReportProjection> topSales = reportService.getTopSales();
        return ResponseEntity.ok(topSales);
    }
}
