package com.zilpher.webdev2.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zilpher.webdev2.dto.CreateProductRequest;
import com.zilpher.webdev2.dto.OrderResponseDTO;
import com.zilpher.webdev2.dto.PlaceOrderRequest;
import com.zilpher.webdev2.entity.CustomerOrder;
import com.zilpher.webdev2.entity.Product;
import com.zilpher.webdev2.entity.User;
import com.zilpher.webdev2.exception.ConflictException;
import com.zilpher.webdev2.repository.CustomerOrderRepository;
import com.zilpher.webdev2.repository.OrderItemRepository;
import com.zilpher.webdev2.repository.ProductRepository;
import com.zilpher.webdev2.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class Week16ServiceUnitTests {
    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @InjectMocks
    private ProductService productService;

    private User sampleUser;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleUser = new User("student_buyer");
        sampleUser.setId(1L);

        sampleProduct = new Product("Mechanical Keyboard", "KEY-101", new BigDecimal("85.00"), 10);
        sampleProduct.setId(1L);
        sampleProduct.setCategory("Peripherals");
    }

    @Test
    void rejectsOrderWhenRequestedQuantityExceedsAvailableStock() {
        sampleProduct.setStockQuantity(2);
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setUserId(1L);
        request.setProductId(1L);
        request.setQuantity(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleProduct));

        ConflictException thrown = assertThrows(ConflictException.class, () -> orderService.placeOrder(request));

        assertTrue(thrown.getMessage().contains("Insufficient stock"));
        assertEquals(2, sampleProduct.getStockQuantity());
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(CustomerOrder.class));
    }

    @Test
    void deductsStockAndCreatesConfirmedOrderWhenStockIsSufficient() {
        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setUserId(1L);
        request.setProductId(1L);
        request.setQuantity(3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> {
            CustomerOrder saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        OrderResponseDTO result = orderService.placeOrder(request);

        assertNotNull(result);
        assertEquals(7, sampleProduct.getStockQuantity());
        verify(productRepository).save(sampleProduct);
        verify(orderRepository).save(any(CustomerOrder.class));
    }

    @Test
    void rejectsProductCreationWhenSkuAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Duplicate Keyboard");
        request.setSku("KEY-101");
        request.setCategory("Peripherals");
        request.setPrice(new BigDecimal("85.00"));
        request.setStockQuantity(5);

        when(productRepository.existsBySku("KEY-101")).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> productService.create(request));

        assertEquals("SKU_EXISTS", thrown.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void blocksProductDeletionWhenReferencedByExistingOrders() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.existsByProductId(1L)).thenReturn(true);

        ConflictException thrown = assertThrows(ConflictException.class, () -> productService.deleteById(1L));

        assertTrue(thrown.getMessage().contains("referenced in existing customer orders"));
        verify(productRepository, never()).deleteById(any());
    }
}