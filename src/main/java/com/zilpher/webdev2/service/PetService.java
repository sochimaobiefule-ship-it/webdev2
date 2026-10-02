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

    public Pet getPetByMicrochip(String microchipNumber) {
        return petRepository.findByMicrochipNumber(microchipNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pet not found with microchip: " + microchipNumber));
    }

    @Transactional
    public void batchRegister(List<Pet> pets) {
        for (Pet p : pets) {
            petRepository.save(p);
        }
    }

    public List<Pet> getPetsBySpecies(String species) {
        return petRepository.findBySpeciesIgnoreCase(species);
    }

    public List<Pet> searchPetsByName(String name) {
        return petRepository.findByNameContainingIgnoreCase(name);
    }

    public List<String> getAllDistinctSpecies() {
        return petRepository.findDistinctSpecies();
    }
}