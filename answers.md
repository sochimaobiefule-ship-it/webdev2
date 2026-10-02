WEBDEV2 — Week 8 Lab Answers

Name: Obiefule Declan

Student ID: 2510223

GitHub repo: https://github.com/sochimaobiefule-ship-it/webdev2

---

## Task 1 — Create the Repository Interface

### Approach / explanation:
I created the Pet entity and added primary key and column constraints to it. Then, I created `PetRepository` extending `JpaRepository<Pet, Long>`. Spring Data JPA automatically handles database operations so I did not need to write raw SQL.

### Code / query used:

**Pet.java:**
```java
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
PetRepository.java:

Java
package com.zilpher.webdev2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.zilpher.webdev2.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}
Evidence:
![Conceptual ERD](task1_output.png)

Task 2 — Implement Full CRUD via a Service Layer
Approach / explanation:
I created PetService to handle business logic and PetController to expose REST endpoints. The service validates null inputs before saving to the database. I tested POST, GET, PUT, and DELETE operations using HTTP requests.

Code / query used:
PetService.java:

Java
package com.zilpher.webdev2.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.zilpher.webdev2.model.Pet;
import com.zilpher.webdev2.repository.PetRepository;

@Service
public class PetService {
    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet registerPet(Pet pet) {
        if (pet == null || pet.getMicrochipNumber() == null || pet.getMicrochipNumber().trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Microchip number cannot be null or empty"
            );
        }
        return petRepository.save(pet);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pet not found with ID: " + id));
    }

    public Pet updatePet(Long id, Pet updatedPet) {
        Pet existingPet = getPetById(id);
        existingPet.setMicrochipNumber(updatedPet.getMicrochipNumber());
        existingPet.setName(updatedPet.getName());
        existingPet.setSpecies(updatedPet.getSpecies());
        existingPet.setBreed(updatedPet.getBreed());
        return petRepository.save(existingPet);
    }

    public void removePet(Long id) {
        Pet existingPet = getPetById(id);
        petRepository.deleteById(existingPet.getId());
    }
}
PetController.java:

Java
package com.zilpher.webdev2.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.zilpher.webdev2.model.Pet;
import com.zilpher.webdev2.service.PetService;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pet registerPet(@RequestBody Pet pet) {
        return petService.registerPet(pet);
    }

    @GetMapping
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public Pet getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable Long id) {
        petService.removePet(id);
    }
}
Evidence:
![Conceptual ERD](task2_output.png)

Task 3 — Write Derived & Custom Query Methods
Approach / explanation:
I added query methods in PetRepository to search pets by species, search pets by name, and list unique species. I then added matching service methods and GET endpoints in PetController.

Code / query used:
PetRepository.java methods:

Java
List<Pet> findBySpeciesIgnoreCase(String species);

List<Pet> findByNameContainingIgnoreCase(String name);

@Query("SELECT DISTINCT p.species FROM Pet p")
List<String> findDistinctSpecies();
PetService.java methods:

Java
public List<Pet> getPetsBySpecies(String species) {
    return petRepository.findBySpeciesIgnoreCase(species);
}

public List<Pet> searchPetsByName(String name) {
    return petRepository.findByNameContainingIgnoreCase(name);
}

public List<String> getAllDistinctSpecies() {
    return petRepository.findDistinctSpecies();
}
PetController.java endpoints:

Java
@GetMapping("/species")
public List<Pet> getPetsBySpecies(@RequestParam String name) {
    return petService.getPetsBySpecies(name);
}

@GetMapping("/search")
public List<Pet> searchPetsByName(@RequestParam String keyword) {
    return petService.searchPetsByName(keyword);
}

@GetMapping("/species-list")
public List<String> getAllDistinctSpecies() {
    return petService.getAllDistinctSpecies();
}
Evidence:
![Conceptual ERD](task3_output.png)

Task 4 — Apply @Transactional and Observe Rollback
Approach / explanation:
I added @Transactional to batchRegister in PetService. This ensures that if any item in a batch import fails, all previous saves in that transaction are cancelled and rolled back.

Code / query used:
PetService.java batch method:

Java
@Transactional
public void batchRegister(List<Pet> pets) {
    for (Pet p : pets) {
        petRepository.save(p);
    }
}
PetController.java batch endpoint:

Java
@PostMapping("/batch")
@ResponseStatus(HttpStatus.CREATED)
public void batchRegister(@RequestBody List<Pet> pets) {
    petService.batchRegister(pets);
}
Evidence:
![Conceptual ERD](task4_output.png)

Observation:
I sent a batch request where the second pet had a duplicate microchip number. The request failed with an exception. I checked MySQL, and the first pet was not saved because the transaction rolled back.

Task 5 — Debug a Persistence Error
Approach / explanation:
I reproduced the persistence failure by invoking save() on an uninitialized Pet object using reproduceTask5Error(repository); in DataInitializer.java. I extracted the root cause from the terminal output logs, documented the stack trace, and verified the successful fix.

Debugging Write-up:
Primary Exception Type: jakarta.validation.ConstraintViolationException

Root Cause Analysis: Hibernate triggers BeanValidationEventListener at the preInsert phase prior to generating or executing SQL INSERT statements in MySQL. Because the Pet entity utilizes Jakarta Bean Validation annotations (@NotBlank on microchipNumber, name, and species), saving an empty entity (new Pet()) fails bean validation in memory. The engine reported three specific constraint violations:

Species is required (property species)

Microchip number is required (property microchipNumber)

Name is required (property name)

SQL Log Confirmation: Validation failed client-side before reaching the database level. As a result, Hibernate aborted transaction processing and no SQL INSERT statements were executed.

Applied Fixes: I ensured @GeneratedValue(strategy = GenerationType.IDENTITY) was properly applied to the entity ID, instantiated complete Pet objects containing all required properties before invoking persistence methods, and commented out the failure reproduction line in DataInitializer.java.

Stack Trace Snippet:
Plaintext
jakarta.validation.ConstraintViolationException: Validation failed for classes [com.zilpher.webdev2.model.Pet] during persist time for groups [jakarta.validation.groups.Default, ]
List of constraint violations:[
	ConstraintViolationImpl{interpolatedMessage='Species is required', propertyPath=species, rootBeanClass=class com.zilpher.webdev2.model.Pet, messageTemplate='Species is required'}
	ConstraintViolationImpl{interpolatedMessage='Microchip number is required', propertyPath=microchipNumber, rootBeanClass=class com.zilpher.webdev2.model.Pet, messageTemplate='Microchip number is required'}
	ConstraintViolationImpl{interpolatedMessage='Name is required', propertyPath=name, rootBeanClass=class com.zilpher.webdev2.model.Pet, messageTemplate='Name is required'}
]
	at org.hibernate.boot.beanvalidation.BeanValidationEventListener.validate(BeanValidationEventListener.java:151) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at org.hibernate.boot.beanvalidation.BeanValidationEventListener.onPreInsert(BeanValidationEventListener.java:81) ~[hibernate-core-6.4.4.Final.jar:6.4.4.Final]
	at com.zilpher.webdev2.config.DataInitializer.reproduceTask5Error(DataInitializer.java:37) ~[classes/:na]
Verified Application Startup Output:
Plaintext
--> Smoke Test - Found 4 pets in database:
    [ID: 1] Buddy (Dog, Golden Retriever) - Chip: 985141000123456
    [ID: 2] Milo (Cat, Siamese) - Chip: 985141000654321
    [ID: 3] Luna (Dog, Poodle) - Chip: 985141000999888
    [ID: 8] Max (Dog, German Shepherd) - Chip: 985141000111222
Code / query used:
Fixed Pet.java entity:

Java
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
}
Evidence:
![Conceptual ERD](task5_outputs)

Self-Check
[] All tasks committed with individual, meaningful commit messages

[] All code on week8 branch

[] This file completed and named answers.md

[] Repository link pasted into Moodle (no files uploaded)