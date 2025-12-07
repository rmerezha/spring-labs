package com.rmerezha.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SalesReportProjection {

    String getProductName();
    String getCategory();
    BigDecimal getProductPrice();

    Integer getQuantitySold();

    LocalDateTime getOrderDate();
    String getOrderStatus();
}
