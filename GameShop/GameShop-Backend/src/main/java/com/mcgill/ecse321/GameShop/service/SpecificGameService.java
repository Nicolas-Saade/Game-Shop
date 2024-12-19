package com.mcgill.ecse321.GameShop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.SpecificGameRepository;

import jakarta.transaction.Transactional;

@Service
public class SpecificGameService {

    @Autowired
    private SpecificGameRepository specificGameRepository;

    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;

    @Transactional
    public SpecificGame createSpecificGame(Game game) {
        // Check if the provided game is null
        if (game == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game cannot be null");
        }

        // Create a new SpecificGame instance and set its status to Confirmed
        SpecificGame specificGame = new SpecificGame(game);
        specificGame.setItemStatus(SpecificGame.ItemStatus.Confirmed);
        specificGameRepository.save(specificGame);
        return specificGame;
    }

    @Transactional
    public SpecificGame findSpecificGameById(int specificGame_id) {
        // Validate the specificGame_id
        if (specificGame_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "SpecificGame ID must be greater than 0");
        }
        // Retrieve the SpecificGame by its ID
        SpecificGame specificGame = specificGameRepository.findById(specificGame_id);
        if (specificGame == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, String.format("SpecificGame does not exist"));
        }
        return specificGame;
    }

    @Transactional
    public SpecificGame updateSpecificGame(int specificGame_id, int game_id) {
        // Validate the specificGame_id and game_id
        if (specificGame_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "SpecificGame ID must be greater than 0");
        }
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        // Retrieve the SpecificGame and Game instances
        SpecificGame specificGame = findSpecificGameById(specificGame_id);
        if (specificGame == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "SpecificGame does not exist");
        }
        Game game = gameService.findGameById(game_id);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Game does not exist");
        }
        // Update the SpecificGame with the new Game
        specificGame.setGames(game);
        specificGameRepository.save(specificGame);
        return specificGame;
    }

    @Transactional
    public Iterable<SpecificGame> getSpecificGamesByGameId(int game_id) {
        // Validate the game_id
        if (game_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be greater than 0");
        }
        if (gameRepository.findById(game_id) == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Game does not exist");
        }

        // Retrieve all SpecificGames
        Iterable<SpecificGame> specificGames = this.getAllSpecificGames();
        if (specificGames == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "No SpecificGame found at all");
        }

        // Filter SpecificGames by the provided game_id
        List<SpecificGame> specificGameList = new ArrayList<>();
        for (SpecificGame specificGame : specificGames) {
            if (specificGame.getGames().getGame_id() == game_id) {
                specificGameList.add(specificGame);
            }
        }

        // Throw an exception only if no specific games match the given game_id
        if (specificGameList.isEmpty()) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "No SpecificGame found for this Game ID");
        }

        return specificGameList;
    }

    @Transactional
    public Iterable<SpecificGame> getAllSpecificGames() {
        // Retrieve all SpecificGames from the repository
        Iterable<SpecificGame> specificGames = specificGameRepository.findAll();
        if (!specificGames.iterator().hasNext()) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "No SpecificGame found at all");
        }
        return specificGames;
    }

    @Transactional
    public SpecificGame updateSpecificGameItemStatus(int specificGame_id, SpecificGame.ItemStatus newItemStatus) {
        // Validate the specificGame_id and newItemStatus
        if (specificGame_id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "SpecificGame ID must be greater than 0");
        }
        if (newItemStatus == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "ItemStatus cannot be null");
        }
        // Retrieve the SpecificGame by its ID
        SpecificGame specificGame = findSpecificGameById(specificGame_id);
        if (specificGame == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "SpecificGame does not exist");
        }

        // Update the ItemStatus of the SpecificGame
        specificGame.setItemStatus(newItemStatus);
        specificGameRepository.save(specificGame);
        return specificGame;
    }

}