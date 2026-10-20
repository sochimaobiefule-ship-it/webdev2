package com.zilpher.webdev2.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zilpher.webdev2.dto.CreateProductRequest;
import com.zilpher.webdev2.dto.ProductResponseDTO;
import com.zilpher.webdev2.dto.UpdateProductRequest;
import com.zilpher.webdev2.entity.Product;
import com.zilpher.webdev2.exception.ResourceNotFoundException;
import com.zilpher.webdev2.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponseDTO> findById(Long id) {
        return productRepository.findById(id).map(this::toDTO);
    }

    public Optional<ProductResponseDTO> updateFull(Long id, UpdateProductRequest request) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setSku(request.getSku());
            existing.setPrice(request.getPrice());
            existing.setStockQuantity(request.getStockQuantity());
            Product updated = productRepository.save(existing);
            return toDTO(updated);
        });
    }

    public Optional<ProductResponseDTO> updatePartial(Long id, UpdateProductRequest request) {
        return productRepository.findById(id).map(existing -> {
            if (request.getName() != null) {
                existing.setName(request.getName());
            }
            if (request.getSku() != null) {
                existing.setSku(request.getSku());
            }
            if (request.getPrice() != null) {
                existing.setPrice(request.getPrice());
            }
            if (request.getStockQuantity() != null) {
                existing.setStockQuantity(request.getStockQuantity());
            }
            Product updated = productRepository.save(existing);
            return toDTO(updated);
        });
    }

    public boolean delete(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public ProductResponseDTO toDTO(Product entity) {
        return new ProductResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getSku(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.getCategory()
        );
    }

    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toResponseDTO(product);
    }

    public ProductResponseDTO create(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU_EXISTS");
        }
        Product entity = new Product();
        entity.setName(request.getName());
        entity.setSku(request.getSku());
        entity.setCategory(request.getCategory()); 
        entity.setPrice(request.getPrice());
        entity.setStockQuantity(request.getStockQuantity());

        Product saved = productRepository.save(entity);
        return toDTO(saved);
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory()
        );
    }

    public Page<ProductResponseDTO> getAll(String category, Pageable pageable) {
        Page<Product> products;
        if (category != null && !category.isBlank()) {
            products = productRepository.findByCategory(category, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        return products.map(this::toResponseDTO);
    }
}