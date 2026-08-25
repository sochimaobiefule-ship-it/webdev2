package com.zilpher.webdev2;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebDev2Application {
    public static void main(String[] args) {
        SpringApplication.run(WebDev2Application.class, args);
    }

    @Bean
    public CommandLineRunner runReport(ProductService productService) {
        return args -> {
            double threshold = 5000.0;
            List<Product> expensiveProducts = productService.getProductsAbovePrice(threshold);

            System.out.println("==========================================");
            System.out.println("            PRODUCT REPORT                ");
            System.out.println("==========================================");
            System.out.println("Shop: " + productService.getShopName());
            System.out.println("Currency: " + productService.getShopCurrency());
            System.out.println();
            System.out.println("Products above " + productService.getShopCurrency() + " " + (int) threshold + ":");
            System.out.println();

            for (Product product : expensiveProducts) {
                System.out.println(product.getName() + " - " + productService.getShopCurrency() + " " + (int) product.getPrice());
            }
        };
    }
}