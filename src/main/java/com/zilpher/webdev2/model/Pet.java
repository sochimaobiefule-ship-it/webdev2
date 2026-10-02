package com.zilpher.webdev2.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Microchip number is required")
    @Pattern(regexp = "^[0-9]{15}$", message = "Microchip number must be exactly 15 digits")
    @Column(unique = true, nullable = false)
    private String microchipNumber;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Species is required")
    @Column(nullable = false)
    private String species;

    private String breed;

    public Pet() {}

    public Pet(String microchipNumber, String name, String species, String breed) {
        this.microchipNumber = microchipNumber;
        this.name = name;
        this.species = species;
        this.breed = breed;
    }

    public Long getId() { 
        return id; 
    }public String getMicrochipNumber() { 
        return microchipNumber; 
    }public String getName() { 
        return name; 
    }public String getSpecies() { 
        return species; 
    }public String getBreed() { 
        return breed; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setMicrochipNumber(String microchipNumber) { 
        this.microchipNumber = microchipNumber; 
    }public void setName(String name) { 
        this.name = name; 
    }public void setSpecies(String species) { 
        this.species = species; 
    }public void setBreed(String breed) { 
        this.breed = breed; 
    }
}