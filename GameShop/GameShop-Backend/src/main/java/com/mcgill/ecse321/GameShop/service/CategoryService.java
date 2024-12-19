package com.mcgill.ecse321.GameShop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.repository.CategoryRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private GameRepository gameRepository;

    // Method to create a new category
    @Transactional
    public Category createCategory(String categoryName, String managerEmail) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Category name cannot be empty or null");
        }
        if (managerEmail == null || managerEmail.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Manager email cannot be empty or null");
        }

        Manager manager = managerRepository.findByEmail(managerEmail);
        if (manager == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("There is no manager with email: %s", managerEmail));
        }
        Category category = new Category(categoryName, manager);
        return categoryRepository.save(category);
    }

    // Method to get a category by its ID
    @Transactional
    public Category getCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Invalid category ID");
        }
        Category category = categoryRepository.findById(categoryId);
        if (category == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Category does not exist");
        }
        return category;
    }

    // Method to get all categories
    @Transactional
    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Method to update a category's name
    @Transactional
    public Category updateCategory(int categoryId, String categoryName) {
        if (categoryId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Invalid category ID");
        }
        Category category = getCategory(categoryId);
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Category name cannot be empty or null");
        }
        category.setCategoryName(categoryName);
        return categoryRepository.save(category);
    }

    // Method to delete a category
    @Transactional
    public void deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Invalid category ID");
        }
        Category category = getCategory(categoryId);
        List<Game> gamesInCategory = gameRepository.findAllByCategoriesContains(category);
        for (Game game : gamesInCategory) {
            List<Category> categories = new ArrayList<>(game.getCategories());
            categories.remove(category);
            game.setCategories(categories);
            gameRepository.save(game); // Save to update the association in the join table
        }
        category.removeManager();
        categoryRepository.save(category);
        categoryRepository.delete(category);
    }

    // Method to get all games in a specific category
    @Transactional
    public List<Game> getAllGamesInCategory(int categoryId) {
        Category category = getCategory(categoryId); // Ensure category exists
        List<Game> games = gameRepository.findAllByCategoriesContains(category);
        return games;
    }
}
