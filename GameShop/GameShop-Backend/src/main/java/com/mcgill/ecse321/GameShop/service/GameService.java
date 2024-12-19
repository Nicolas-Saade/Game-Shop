package com.mcgill.ecse321.GameShop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Game;

import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.repository.CategoryRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.PlatformRepository;

import jakarta.transaction.Transactional;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PlatformRepository platformRepository;

    // Utility method to check if a string is empty or null
    public boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Transactional
    public Game createGame(String title, String aDescription, double aPrice, Game.GameStatus aGameStatus,
            int aStockQuantity, String aPhotoUrl) {
        // Validate input parameters
        if (isEmpty(title)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Title cannot be empty or null");
        }
        if (isEmpty(aDescription)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Description cannot be empty or null");
        }
        if (aPrice <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Price cannot be negative or 0");
        }
        if (aStockQuantity < 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Stock quantity cannot be negative");
        }
        if (aGameStatus == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game status cannot be null");
        }
        if (isEmpty(aPhotoUrl)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Photo URL cannot be empty or null");
        }
        // Create and save the game
        Game game = new Game(title, aDescription, aPrice, aGameStatus, aStockQuantity, aPhotoUrl);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game findGameById(int game_id) {
        // Validate game ID
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game by ID
        Game game = gameRepository.findById(game_id);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, String.format("Game with ID %d does not exist", game_id));
        }
        return game;
    }

    @Transactional
    public Iterable<Game> getGamesByTitle(String title) {
        // Validate title
        if (isEmpty(title)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Title cannot be empty or null");
        }
        // Find games by title
        Iterable<Game> games = gameRepository.findAllByTitle(title);
        if (!games.iterator().hasNext()) { // Checks if games list is empty
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Game with title %s does not exist", title));
        }
        return games;
    }

    @Transactional
    public Iterable<Game> getGamesByStatus(GameStatus status) {
        // Validate game status
        if (status == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game status cannot be null");
        }
        // Get all games and filter by status
        Iterable<Game> games = this.getAllGames();
        List<Game> gamesByStatus = new ArrayList<Game>();
        for (Game game : games) {
            if (game.getGameStatus().equals(status)) {
                gamesByStatus.add(game);
            }
        }
        if (gamesByStatus.isEmpty()) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "No games with the given status");
        }
        return gamesByStatus;
    }

    @Transactional
    public Iterable<Game> getGamesByStockQuantity(int stockQuantity) {
        // Validate stock quantity
        if (stockQuantity < 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Stock quantity cannot be negative");
        }
        // Get all games and filter by stock quantity
        Iterable<Game> games = this.getAllGames();
        List<Game> gamesByStockQuantity = new ArrayList<Game>();
        for (Game game : games) {
            if (game.getStockQuantity() == stockQuantity) {
                gamesByStockQuantity.add(game);
            }
        }
        if (gamesByStockQuantity.isEmpty()) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "No games with the given stock quantity");
        }
        return gamesByStockQuantity;
    }

    @Transactional
    public Iterable<Game> getAllGames() {
        // Retrieve all games from the repository
        return gameRepository.findAll();
    }

    @Transactional
    public Game updateGameDescription(int game_id, String newDescription) {
        // Validate new description and game ID
        if (isEmpty(newDescription)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Description cannot be empty or null");
        }
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game and update description
        Game game = findGameById(game_id);

        game.setDescription(newDescription);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game updateGamePrice(int game_id, double newPrice) {
        // Validate new price and game ID
        if (newPrice <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Price cannot be negative nor null");
        }
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game and update price
        Game game = findGameById(game_id);

        game.setPrice(newPrice);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game updateGameStockQuantity(int game_id, int newStockQuantity) {
        // Validate new stock quantity and game ID
        if (newStockQuantity < 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Stock quantity cannot be negative");
        }
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game and update stock quantity
        Game game = findGameById(game_id);
        if (newStockQuantity == 0) {
            game.setGameStatus(GameStatus.OutOfStock);
        }

        game.setStockQuantity(newStockQuantity);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game updateGameStatus(int game_id, Game.GameStatus newGameStatus) {
        // Validate new game status and game ID
        if (newGameStatus == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game status cannot be null");
        }
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game and update status
        Game game = findGameById(game_id);

        game.setGameStatus(newGameStatus);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game updateGamePhotoUrl(int game_id, String newPhotoUrl) {
        // Validate new photo URL and game ID
        if (isEmpty(newPhotoUrl)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Photo URL cannot be empty or null");
        }
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game and update photo URL
        Game game = findGameById(game_id);

        game.setPhotoUrl(newPhotoUrl);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game updateGameTitle(int game_id, String newTitle) {
        // Validate new title and game ID
        if (isEmpty(newTitle)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Title cannot be empty or null");
        }
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game and update title
        Game game = findGameById(game_id);

        game.setTitle(newTitle);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game updateCategories(int gameId, List<Integer> categories) {
        // Validate categories and game ID
        if (categories == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Categories cannot be null");
        }
        if (gameId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Find game and update categories
        Game game = findGameById(gameId);
        if (categories.isEmpty()) {
            game.setCategories(new ArrayList<>());
            gameRepository.save(game);
            return game;
        }
        List<Category> categoryList = new ArrayList<>();
        for (int category_id : categories) {
            if (category_id <= 0) {
                throw new GameShopException(HttpStatus.BAD_REQUEST, "Invalid category ID");
            }
            Category category = categoryRepository.findById(category_id);
            if (category == null) {
                throw new GameShopException(HttpStatus.NOT_FOUND, "Category does not exist");
            }

            categoryList.add(category);

        }
        game.setCategories(categoryList);

        gameRepository.save(game);
        return game;
    }

    public Game updatePlatforms(int gameId, List<Integer> platforms) {
        // Validate game ID
        if (gameId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }

        // Find the game by ID

        // If platforms is null, throw an exception (platforms must be provided as a
        // list)
        if (platforms == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Platforms cannot be null");
        }
        Game game = findGameById(gameId);
        // If the provided list is empty, remove all platforms from the game
        if (platforms.isEmpty()) {
            game.setPlatforms(new ArrayList<>()); // Clear the platforms
            gameRepository.save(game);
            return game; // Return the updated game with no platforms
        }

        // Validate and update the platforms
        List<Platform> platformList = new ArrayList<>();
        for (int platform_id : platforms) {
            if (platform_id <= 0) {
                throw new GameShopException(HttpStatus.BAD_REQUEST, "Platform ID must be greater than 0");
            }
            Platform platform = platformRepository.findById(platform_id);
            if (platform == null) {
                throw new GameShopException(HttpStatus.NOT_FOUND, "Platform does not exist");
            }
            platformList.add(platform);
        }

        game.setPlatforms(platformList); // Update the platforms
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game deleteGame(int game_id) {
        // Find game and set its status to Archived
        Game game = findGameById(game_id);
        game.setGameStatus(GameStatus.Archived);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game addCategory(int game_id, int category_id) {
        // Validate game ID and category ID
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        if (category_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Category ID must be greater than 0");
        }
        // Find game and category, then add category to game
        Game game = findGameById(game_id);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, String.format("Game with ID %d does not exist", game_id));
        }

        Category category = categoryRepository.findById(category_id);
        if (category == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Category with ID %d does not exist", category_id));
        }
        if (game.getCategories().contains(category)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Category is already in the game");
        }
        game.addCategory(category);
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public Game addPlatform(int game_id, int platform_id) {
        // Validate game ID and platform ID
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        if (platform_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Platform ID must be greater than 0");
        }

        // Find game and platform, then add platform to game
        Game game = findGameById(game_id);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, String.format("Game with ID %d does not exist", game_id));
        }
        Platform platform = platformRepository.findById(platform_id);
        if (platform == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Platform with ID %d does not exist", platform_id));
        }
        if (game.getPlatforms().contains(platform)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Platform is already in the game");
        }
        game.addPlatform(platform);
        gameRepository.save(game);
        return game;
    }
}
