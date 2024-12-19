package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
   public Category findById(int category_id);
}