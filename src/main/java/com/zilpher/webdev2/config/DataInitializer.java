package com.zilpher.webdev2.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zilpher.webdev2.model.Pet;
import com.zilpher.webdev2.repository.PetRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initDatabase(PetRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Pet("985141000123456", "Buddy", "Dog", "Golden Retriever"));
                repository.save(new Pet("985141000654321", "Milo", "Cat", "Siamese"));
                repository.save(new Pet("985141000999888", "Luna", "Dog", "Poodle"));
                System.out.println("--> DataInitializer: Sample pet records seeded successfully.");
            }
            
            System.out.println("--> Smoke Test - Found " + repository.findAll().size() + " pets in database:");
            repository.findAll().forEach(p -> 
                System.out.println("    [ID: " + p.getId() + "] " + p.getName() + " (" + p.getSpecies() + ", " + p.getBreed() + ") - Chip: " + p.getMicrochipNumber())
            );
        };
    }
}