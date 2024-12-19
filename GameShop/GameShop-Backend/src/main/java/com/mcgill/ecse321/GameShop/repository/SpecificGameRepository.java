package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.SpecificGame;

public interface SpecificGameRepository extends CrudRepository<SpecificGame, Integer> {
   public SpecificGame findById(int specificGame_id);
}