package com.zilpher.webdev2.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zilpher.webdev2.controller.OrderController;
import com.zilpher.webdev2.controller.ProductController;
import com.zilpher.webdev2.dto.CreateProductRequest;
import com.zilpher.webdev2.dto.ProductResponseDTO;
import com.zilpher.webdev2.exception.ConflictException;
import com.zilpher.webdev2.exception.GlobalExceptionHandler;
import com.zilpher.webdev2.exception.ResourceNotFoundException;
import com.zilpher.webdev2.security.SecurityConfig;

@WebMvcTest(controllers = { ProductController.class, OrderController.class })
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
class Week16ApiSecurityTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private OrderService orderService;

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Test
    void anonymousGetProductsReturns401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    @WithMockUser(username = "reader_user", roles = { "READER" })
    void readerPostProductWithValidCsrfReturns403Forbidden() throws Exception {
        String body = """
                {
                  "name": "Unauthorized Item",
                  "sku": "UNAUTH-01",
                  "category": "Audit",
                  "price": 15.00,
                  "stockQuantity": 3
                }
                """;

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    @WithMockUser(username = "manager_user", roles = { "MANAGER" })
    void managerPostProductWithValidCsrfReturns201Created() throws Exception {
        ProductResponseDTO created = new ProductResponseDTO(
                10L, "USB-C Dock", "DOCK-500", new BigDecimal("79.99"), 12, "Accessories");
        when(productService.create(any(CreateProductRequest.class))).thenReturn(created);

        String body = """
                {
                  "name": "USB-C Dock",
                  "sku": "DOCK-500",
                  "category": "Accessories",
                  "price": 79.99,
                  "stockQuantity": 12
                }
                """;

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.sku").value("DOCK-500"));
    }

    @Test
    @WithMockUser(username = "manager_user", roles = { "MANAGER" })
    void managerPostInvalidProductReturns400ValidationError() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "sku": "",
                  "category": "",
                  "price": -5.00,
                  "stockQuantity": -1
                }
                """;

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @WithMockUser(username = "manager_user", roles = { "MANAGER" })
    void managerGetMissingProductReturns404NotFound() throws Exception {
        when(productService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    @Test
    @WithMockUser(username = "manager_user", roles = { "MANAGER" })
    void managerDeleteReferencedProductReturns409Conflict() throws Exception {
        doThrow(new ConflictException("Cannot delete product ID 1 because it is referenced in existing customer orders."))
                .when(productService).deleteById(1L);

        mockMvc.perform(delete("/api/products/1")
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}