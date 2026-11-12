package com.zilpher.webdev2.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zilpher.webdev2.dto.OrderResponseDTO;
import com.zilpher.webdev2.dto.PlaceOrderRequest;
import com.zilpher.webdev2.entity.CustomerOrder;
import com.zilpher.webdev2.entity.OrderItem;
import com.zilpher.webdev2.entity.Product;
import com.zilpher.webdev2.entity.User;
import com.zilpher.webdev2.exception.ConflictException;
import com.zilpher.webdev2.exception.ResourceNotFoundException;
import com.zilpher.webdev2.repository.CustomerOrderRepository;
import com.zilpher.webdev2.repository.ProductRepository;
import com.zilpher.webdev2.repository.UserRepository;

@Service
public class OrderService {
    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(CustomerOrderRepository orderRepository,
            UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponseDTO placeOrder(PlaceOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Product product = productRepository.findByIdForUpdate(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new ConflictException("Insufficient stock for '" + product.getName()
                + "'. Requested: " + request.getQuantity() + ", Available: " + product.getStockQuantity());
        }

        int updatedStock = product.getStockQuantity() - request.getQuantity();
        product.setStockQuantity(updatedStock);
        productRepository.save(product);

        if (request.isSimulateFailure()) {
            throw new IllegalStateException("Simulated failure after stock deduction — transaction rolled back.");
        }

        double unitPrice = product.getPrice().doubleValue();
        CustomerOrder order = new CustomerOrder(LocalDate.now(), "CONFIRMED");
        order.setUser(user);

        OrderItem item = new OrderItem(request.getQuantity(), unitPrice);
        item.setProduct(product);
        order.addOrderItem(item);

        CustomerOrder savedOrder = orderRepository.save(order);

        return toDTO(savedOrder, product, item);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> !order.getOrderItems().isEmpty())
                .map(order -> {
                    OrderItem item = order.getOrderItems().get(0);
                    return toDTO(order, item.getProduct(), item);
                })
                .collect(Collectors.toList());
    }

    private OrderResponseDTO toDTO(CustomerOrder order, Product product, OrderItem item) {
        double totalAmount = item.getQuantity() * item.getPrice();
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                product.getId(),
                product.getName(),
                item.getQuantity(),
                item.getPrice(),
                totalAmount,
                product.getStockQuantity()
        );
    }
}