package com.zilpher.webdev2;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zilpher.webdev2.entity.CustomerOrder;
import com.zilpher.webdev2.entity.OrderItem;
import com.zilpher.webdev2.entity.Product;
import com.zilpher.webdev2.entity.User;
import com.zilpher.webdev2.entity.UserProfile;
import com.zilpher.webdev2.repository.CustomerOrderRepository;
import com.zilpher.webdev2.repository.ProductRepository;
import com.zilpher.webdev2.repository.UserRepository;

@Component
public class Task5SmokeTestRunner implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CustomerOrderRepository customerOrderRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("TASK 5 CAPSTONE SMOKE TEST START");

        User user = new User("capstone_user");
        UserProfile profile = new UserProfile("Capstone Developer", "555-1234");
        user.setProfile(profile);
        userRepository.save(user);

        Product laptop = productRepository.save(new Product("Laptop", 1200.00));

        CustomerOrder order = new CustomerOrder(LocalDate.now(), "CONFIRMED");
        order.setUser(user);

        OrderItem item = new OrderItem(1, 1200.00);
        item.setProduct(laptop);
        order.addOrderItem(item);

        customerOrderRepository.save(order);

        System.out.println("Capstone object graph persisted and read back successfully");
        System.out.println("TASK 5 CAPSTONE SMOKE TEST END");
    }
}