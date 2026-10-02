package com.zilpher.webdev2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zilpher.webdev2.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    Optional<Pet> findByMicrochipNumber(String microchipNumber);
    List<Pet> findBySpeciesIgnoreCase(String species);
    List<Pet> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT p.species FROM Pet p")
    List<String> findDistinctSpecies();
}