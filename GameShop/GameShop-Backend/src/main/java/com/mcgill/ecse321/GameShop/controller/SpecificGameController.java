package com.mcgill.ecse321.GameShop.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameListDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameRequestDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameResponseDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameSummaryDto;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.model.SpecificGame.ItemStatus;
import com.mcgill.ecse321.GameShop.service.GameService;
import com.mcgill.ecse321.GameShop.service.SpecificGameService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class SpecificGameController {

    @Autowired
    private SpecificGameService specificGameService;

    @Autowired
    private GameService gameService;

    /**
     * Create a new SpecificGame.
     * 
     * @param request the specific game request data transfer object containing the
     *                details of the specific game to be created
     * @return the response data transfer object containing the details of the
     *         created specific game
     */
    @PostMapping("/specificGames")
    public SpecificGameResponseDto createSpecificGame(@Valid @RequestBody SpecificGameRequestDto request) {
        // Create the specific game
        SpecificGame createdSpecificGame = specificGameService
                .createSpecificGame(gameService.findGameById(request.getGame_id()));
        return SpecificGameResponseDto.create(createdSpecificGame);
    }

    /**
     * Get a SpecificGame by ID.
     * 
     * @param specificGame_id the ID of the specific game to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved specific game
     */
    @GetMapping("/specificGames/{specificGame_id}")
    public SpecificGameResponseDto findSpecificGameById(@PathVariable int specificGame_id) {
        // Get the specific game by its id
        SpecificGame specificGame = specificGameService.findSpecificGameById(specificGame_id);
        return SpecificGameResponseDto.create(specificGame);
    }

    /**
     * Get all SpecificGames.
     * 
     * @return a list data transfer object containing the summaries of all specific
     *         games
     */
    @GetMapping("/specificGames")
    public SpecificGameListDto findAllSpecificGames() {
        List<SpecificGameSummaryDto> dtos = new ArrayList<>();

        // Get all specific games and add them to dtos list
        for (SpecificGame specificGame : specificGameService.getAllSpecificGames()) {
            dtos.add(new SpecificGameSummaryDto(specificGame));
        }

        return new SpecificGameListDto(dtos);
    }

    /**
     * Update SpecificGame ItemStatus.
     * 
     * @param specificGame_id the ID of the specific game to update
     * @param newItemStatus   the new item status to set for the specific game
     * @return the response data transfer object containing the details of the
     *         updated specific game
     */
    @PutMapping("/specificGames/{specificGame_id}/itemStatus")
    public SpecificGameResponseDto updateSpecificGameItemStatus(
            @PathVariable int specificGame_id,
            @RequestBody ItemStatus newItemStatus) {
        SpecificGame updatedSpecificGame = specificGameService.updateSpecificGameItemStatus(specificGame_id,
                newItemStatus);
        return SpecificGameResponseDto.create(updatedSpecificGame);
    }

    /**
     * Retrieves a list of specific games associated with a given game ID.
     *
     * @param game_id The ID of the game for which specific games are to be
     *                retrieved.
     * @return A SpecificGameListDto containing a list of SpecificGameSummaryDto
     *         objects.
     */
    @GetMapping("/games/{game_id}/specificGames")
    public SpecificGameListDto getSpecificGamesByGameId(@PathVariable int game_id) {
        Iterable<SpecificGame> specificGames = specificGameService.getSpecificGamesByGameId(game_id);
        List<SpecificGameSummaryDto> dtos = new ArrayList<>();

        // Loop through the specific games and add them to the list
        for (SpecificGame specificGame : specificGames) {
            dtos.add(new SpecificGameSummaryDto(specificGame));
        }
        return new SpecificGameListDto(dtos);
    }

    /**
     * Updates a specific game with the given specific game ID and game ID.
     *
     * @param specificGame_id the ID of the specific game to be updated
     * @param game_id         the ID of the game to update the specific game with
     * @return a response DTO containing the updated specific game details
     */
    @PutMapping("/specificGames/{specificGame_id}/game/{game_id}")
    public SpecificGameResponseDto updateSpecificGame(@PathVariable int specificGame_id, @PathVariable int game_id) {
        SpecificGame updatedSpecificGame = specificGameService.updateSpecificGame(specificGame_id, game_id);
        return SpecificGameResponseDto.create(updatedSpecificGame);
    }

}
