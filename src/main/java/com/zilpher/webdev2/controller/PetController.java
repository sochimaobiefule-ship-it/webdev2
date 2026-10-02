package com.zilpher.webdev2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zilpher.webdev2.model.Pet;
import com.zilpher.webdev2.service.PetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> registerPet(@Valid @RequestBody Pet pet) {
        return new ResponseEntity<>(petService.registerPet(pet), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        return ResponseEntity.ok(petService.updatePet(id, pet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removePet(@PathVariable Long id) {
        petService.removePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chip/{microchipNumber}")
    public ResponseEntity<Pet> getPetByMicrochip(@PathVariable String microchipNumber) {
        return ResponseEntity.ok(petService.getPetByMicrochip(microchipNumber));
    }

    @GetMapping("/species")
    public ResponseEntity<List<Pet>> getPetsBySpecies(@RequestParam String species) {
        return ResponseEntity.ok(petService.getPetsBySpecies(species));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Pet>> searchPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String name) {
        if (species != null && !species.isBlank()) {
            return ResponseEntity.ok(petService.getPetsBySpecies(species));
        } else if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(petService.searchPetsByName(name));
        }
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/species-list")
    public ResponseEntity<List<String>> getAllDistinctSpecies() {
        return ResponseEntity.ok(petService.getAllDistinctSpecies());
    }
}