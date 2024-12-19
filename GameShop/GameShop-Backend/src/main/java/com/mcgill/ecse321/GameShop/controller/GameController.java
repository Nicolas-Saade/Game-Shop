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

import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.service.GameService;
import com.mcgill.ecse321.GameShop.service.SpecificGameService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private SpecificGameService specificGameService;

    /**
     * Find a game by its ID.
     * 
     * @param game_id the ID of the game to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved game
     */
    @GetMapping("/games/{game_id}")
    public GameResponseDto findGameById(@PathVariable int game_id) {
        // Find the game by its ID
        Game game = gameService.findGameById(game_id);
        return GameResponseDto.create(game);
    }

    /**
     * Find all games.
     * 
     * @return a list data transfer object containing the summaries of all games
     */
    @GetMapping("/games")
    public GameListDto findAllGames() {
        List<GameSummaryDto> dtos = new ArrayList<>();

        // Get all games and add them to the list
        for (Game game : gameService.getAllGames()) {
            dtos.add(new GameSummaryDto(game));
        }

        return new GameListDto(dtos);
    }

    /**
     * Create a new game.
     * 
     * @param request the game request data transfer object containing the details
     *                of the game to be created
     * @return the response data transfer object containing the details of the
     *         created game
     */
    @PostMapping("/games")
    public GameResponseDto createGame(@Valid @RequestBody GameRequestDto request) {
        // Create the game
        Game createdGame = gameService.createGame(request.getaTitle(), request.getaDescription(), request.getaPrice(),
                request.getaGameStatus(), request.getaStockQuantity(), request.getaPhotoUrl());

        // Update the categories and platforms of the game
        if (request.getCategories() != null) {
            gameService.updateCategories(createdGame.getGame_id(), request.getCategories());
        }
        if (request.getPlatforms() != null) {
            gameService.updatePlatforms(createdGame.getGame_id(), request.getPlatforms());
        }

        return GameResponseDto.create(createdGame);
    }

    /**
     * Delete a game by its ID.
     * 
     * @param game_id the ID of the game to delete
     */
    @DeleteMapping("/games/{game_id}")
    public void deleteGame(@PathVariable int game_id) {
        gameService.deleteGame(game_id);
    }

    /**
     * Update an existing game.
     * 
     * @param id      the ID of the game to update
     * @param request the game request data transfer object containing the updated
     *                details of the game
     * @return the response data transfer object containing the details of the
     *         updated game
     */
    @PutMapping("/games/{id}")
    public GameResponseDto putMethod(@PathVariable int id, @RequestBody GameRequestDto request) {
        Game game = gameService.findGameById(id);

        // Update the game details
        gameService.updateGameDescription(id, request.getaDescription());
        gameService.updateGamePrice(id, request.getaPrice());
        gameService.updateGameStatus(id, request.getaGameStatus());
        gameService.updateGameStockQuantity(id, request.getaStockQuantity());
        gameService.updateGamePhotoUrl(id, request.getaPhotoUrl());
        gameService.updateGameTitle(id, request.getaTitle());
        gameService.updateCategories(id, request.getCategories());
        gameService.updatePlatforms(id, request.getPlatforms());

        return new GameResponseDto(game);
    }

    /**
     * Add a platform to a game.
     * 
     * @param game_id     the ID of the game to add the platform to
     * @param platform_id the ID of the platform to add to the game
     * @return the response data transfer object containing the details of the
     *         updated game
     */
    @PutMapping("/games/platform/{game_id}/{platform_id}")
    public GameResponseDto addPlatform(@PathVariable int game_id, @PathVariable int platform_id) {
        // Add the platform to the game
        gameService.addPlatform(game_id, platform_id);

        return GameResponseDto.create(gameService.findGameById(game_id));
    }

    /**
     * Add a category to a game.
     * 
     * @param game_id     the ID of the game to add the category to
     * @param category_id the ID of the category to add to the game
     * @return the response data transfer object containing the details of the
     *         updated game
     */
    @PutMapping("/games/category/{game_id}/{category_id}")
    public GameResponseDto addCategory(@PathVariable int game_id, @PathVariable int category_id) {
        gameService.addCategory(game_id, category_id);

        return GameResponseDto.create(gameService.findGameById(game_id));
    }

    /**
     * Get games by title.
     * 
     * @param Title the title of the games to retrieve
     * @return a list data transfer object containing the summaries of the games
     *         with the specified title
     */
    @GetMapping("/games/Title/{Title}")
    public GameListDto getGamesByTitle(@PathVariable String Title) {
        Iterable<Game> games = gameService.getGamesByTitle(Title);
        List<GameSummaryDto> dtos = new ArrayList<>();
        // Loop through the titles of the games and add them to the list
        for (Game game : games) {
            dtos.add(new GameSummaryDto(game));
        }
        return new GameListDto(dtos);
    }

    /**
     * Get games by status.
     * 
     * @param status the status of the games to retrieve
     * @return a list data transfer object containing the summaries of the games
     *         with the specified status
     */
    @GetMapping("/games/Status/{status}")
    public GameListDto getGamesByStatus(@PathVariable GameStatus status) {
        Iterable<Game> games = gameService.getGamesByStatus(status);
        List<GameSummaryDto> dtos = new ArrayList<>();
        // Loop through the statuses of the games and add them to the list
        for (Game game : games) {
            dtos.add(new GameSummaryDto(game));
        }
        return new GameListDto(dtos);
    }

    /**
     * Get games by stock quantity.
     * 
     * @param stock_quantity the stock quantity of the games to retrieve
     * @return a list data transfer object containing the summaries of the games
     *         with the specified stock quantity
     */
    @GetMapping("/games/SpecificGame/{stock_quantity}")
    public GameListDto getGamesByStockQuantity(@PathVariable int stock_quantity) {
        Iterable<Game> games = gameService.getGamesByStockQuantity(stock_quantity);
        List<GameSummaryDto> dtos = new ArrayList<>();

        // Loop through the stock quantities of the games and add them to the list
        for (Game game : games) {
            dtos.add(new GameSummaryDto(game));
        }
        return new GameListDto(dtos);
    }

    /**
     * Create specific copies of a game.
     * 
     * @param game_id        the ID of the game to create copies of
     * @param numberOfCopies the number of copies to create
     * @return the response data transfer object containing the details of the
     *         updated game
     */
    @PutMapping("/games/specificGame/{game_id}/{numberOfCopies}")
    public GameResponseDto createSpecificGame(@PathVariable int game_id, @PathVariable int numberOfCopies) {
        // Find the game by its ID
        Game game = gameService.findGameById(game_id);

        // Get the stock quantity of the game and update it
        int count = numberOfCopies + game.getStockQuantity();

        // Update the stock quantity of the game
        gameService.updateGameStockQuantity(game_id, count);

        // Create the specific copies of the game
        for (int i = 0; i < numberOfCopies; i++) {
            specificGameService.createSpecificGame(game);
        }

        return new GameResponseDto(game);
    }

}
