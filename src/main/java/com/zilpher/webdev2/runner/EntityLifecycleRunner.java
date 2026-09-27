package com.zilpher.webdev2.runner;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.zilpher.webdev2.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class EntityLifecycleRunner implements CommandLineRunner {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("\n====================");
        System.out.println("--- ENTITY LIFECYCLE DEMO STARTING ---");
        System.out.println("====================");

        User user = new User("zilpher_user", "original@example.com", LocalDateTime.now());
        System.out.println("1. [Transient] User created: " + user);

        entityManager.persist(user);
        System.out.println("2. [Managed] User persisted with ID: " + user.getId());
        entityManager.flush();

        entityManager.detach(user);
        System.out.println("3. [Detached] User detached from EntityManager.");

        user.setEmail("modified@example.com");
        System.out.println("4. [Modified Detached] Updated local object email to: " + user.getEmail());

        entityManager.flush(); 

        System.out.println("====================");
        System.out.println("--- ENTITY LIFECYCLE DEMO COMPLETED ---");
        System.out.println("====================\n");
    }
}