package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Promotion;

public interface PromotionRepository extends CrudRepository<Promotion, Integer> {
   public Promotion findById(int promotion_id);
}