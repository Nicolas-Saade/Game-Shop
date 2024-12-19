package com.mcgill.ecse321.GameShop.repository;

import java.util.List;


import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Platform;

public interface GameRepository extends CrudRepository<Game, Integer> {
   public Game findById(int game_id);
   public List<Game> findAllByCategoriesContains(Category category);
   public List<Game> findAllByPlatformsContains(Platform platform);
   public List<Game> findAllByTitle(String title);
}