package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Platform;

public interface PlatformRepository extends CrudRepository<Platform, Integer> {
   public Platform findById(int platform_id);
}