package com.mcgill.ecse321.GameShop.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryListDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryRequestDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryResponseDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategorySummaryDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Creates a new category based on the provided request data.
     *
     * @param request the CategoryRequestDto containing the category name and
     *                manager email
     * @return a CategoryResponseDto representing the created category
     */
    @PostMapping("/categories")
    public CategoryResponseDto createCategory(@Valid @RequestBody CategoryRequestDto request) {
        // create the category
        Category category = categoryService.createCategory(request.getCategoryName(), request.getManagerEmail());
        return CategoryResponseDto.create(category);
    }

    /**
     * Get a category by ID.
     * 
     * @param cid the ID of the category to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved category
     */
    @GetMapping("/categories/{cid}")
    public CategoryResponseDto getCategoryById(@PathVariable int cid) {
        // Call service method to get the category
        Category category = categoryService.getCategory(cid);
        return CategoryResponseDto.create(category);
    }

    /**
     * Get all categories.
     * 
     * @return a list data transfer object containing the summaries of all
     *         categories
     */
    @GetMapping("/categories")
    public CategoryListDto getAllCategories() {
        List<CategorySummaryDto> dtos = new ArrayList<>();

        // Loop through all categories and add them to the list
        for (Category category : categoryService.getAllCategories()) {
            dtos.add(new CategorySummaryDto(category));
        }
        return new CategoryListDto(dtos);
    }

    /**
     * Get all games in a category.
     * 
     * @param cid the ID of the category to retrieve games from
     * @return a list data transfer object containing the summaries of all games in
     *         the category
     */
    @GetMapping("/categories/{cid}/games")
    public GameListDto getAllGamesInCategory(@PathVariable int cid) {
        List<GameSummaryDto> dtos = new ArrayList<>();

        // Loop through all games in the category and add them to the list
        for (Game game : categoryService.getAllGamesInCategory(cid)) {
            dtos.add(new GameSummaryDto(game));
        }
        return new GameListDto(dtos);
    }

    /**
     * Update a category's name.
     * 
     * @param id      the ID of the category to update
     * @param request the category request data transfer object containing the
     *                updated name of the category
     * @return the response data transfer object containing the details of the
     *         updated category
     */
    @PutMapping("/categories/{id}")
    public CategoryResponseDto updateCategory(@PathVariable int id, @RequestBody CategoryRequestDto request) {
        // Call update service method
        Category category = categoryService.updateCategory(id, request.getCategoryName());
        return CategoryResponseDto.create(category);
    }

    /**
     * Delete a category.
     * 
     * @param id the ID of the category to delete
     */
    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
    }
}
