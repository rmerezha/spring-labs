package com.rmerezha.web;

import com.rmerezha.PostgresIT;
import com.rmerezha.domain.Category;
import com.rmerezha.domain.OrderStatus;
import com.rmerezha.persistence.entity.OrderEntity;
import com.rmerezha.persistence.entity.OrderItemEntity;
import com.rmerezha.persistence.entity.ProductEntity;
import com.rmerezha.repository.OrderRepository;
import com.rmerezha.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("no-auth")
class ReportControllerIT extends PostgresIT {

    @Autowired
    private MockMvc mockMvc;

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
    @Transactional
    @DisplayName("GET /api/v1/report/sales returns top sales list")
    void shouldReturnTopSales() throws Exception {
        createAndSaveOrderWithProduct("Space Burger", Category.FOOD, 5);
        createAndSaveOrderWithProduct("Galaxy Water", Category.FOOD, 2);

        mockMvc.perform(get("/api/v1/report/sales")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productName", is("Space Burger")))
                .andExpect(jsonPath("$[0].quantitySold", is(5)))
                .andExpect(jsonPath("$[0].category", is("FOOD")))
                .andExpect(jsonPath("$[1].productName", is("Galaxy Water")))
                .andExpect(jsonPath("$[1].quantitySold", is(2)));
    }

    private void createAndSaveOrderWithProduct(String productName, Category category, int quantity) {
        ProductEntity product = new ProductEntity();
        product.setName(productName);
        product.setCategory(category);
        product.setPrice(BigDecimal.valueOf(100));
        product.setStockQuantity(50);
        product.setOriginPlanet("Mars");
        productRepository.save(product);

        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.COMPLETED);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

        OrderItemEntity item = new OrderItemEntity();
        item.setProduct(product);
        item.setQuantity(quantity);

        order.addItem(item);

        orderRepository.save(order);
    }
}
