package com.zilpher.webdev2.integration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.zilpher.webdev2.dto.OrderResponseDTO;
import com.zilpher.webdev2.dto.PlaceOrderRequest;
import com.zilpher.webdev2.entity.Product;
import com.zilpher.webdev2.entity.User;
import com.zilpher.webdev2.repository.CustomerOrderRepository;
import com.zilpher.webdev2.repository.OrderItemRepository;
import com.zilpher.webdev2.repository.ProductRepository;
import com.zilpher.webdev2.repository.UserRepository;
import com.zilpher.webdev2.service.OrderService;

@SpringBootTest
@ActiveProfiles("test")
class Week16MySqlTransactionTests {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerOrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User savedUser;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        String activeDb = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        assertEquals("webdev2_week16_test", activeDb, "Refusing to run integration tests outside webdev2_week16_test");

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User("tx_buyer");
        user.setEmail("tx.buyer@example.test");
        user.setRole("READER");
        savedUser = userRepository.save(user);

        Product product = new Product("Mechanical Keyboard", "TX-KEY-01", new BigDecimal("85.00"), 10);
        product.setCategory("Peripherals");
        savedProduct = productRepository.save(product);
    }

    @Test
    void commitsStockDeductionAndOrderRecordsAcrossRelatedTables() {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setUserId(savedUser.getId());
        request.setProductId(savedProduct.getId());
        request.setQuantity(3);
        request.setSimulateFailure(false);

        OrderResponseDTO response = orderService.placeOrder(request);

        Product reloadedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertNotNull(response.getOrderId());
        assertEquals(7, reloadedProduct.getStockQuantity());
        assertEquals(1L, orderRepository.count());
        assertEquals(1L, orderItemRepository.count());
    }

    @Test
    void rollsBackStockDeductionWhenFailureOccursAfterFirstDatabaseWrite() {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setUserId(savedUser.getId());
        request.setProductId(savedProduct.getId());
        request.setQuantity(4);
        request.setSimulateFailure(true);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> orderService.placeOrder(request));
        assertTrue(thrown.getMessage().contains("Simulated failure after stock deduction"));

        Product reloadedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertEquals(10, reloadedProduct.getStockQuantity());
        assertEquals(0L, orderRepository.count());
        assertEquals(0L, orderItemRepository.count());
    }
}