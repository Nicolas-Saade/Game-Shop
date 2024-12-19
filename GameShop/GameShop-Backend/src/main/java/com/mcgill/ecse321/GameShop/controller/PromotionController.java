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

import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionListDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionRequestDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionResponseDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Promotion;
import com.mcgill.ecse321.GameShop.service.PromotionService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    /**
     * Create a new promotion.
     * 
     * @param request the promotion request data transfer object containing the
     *                details of the promotion to be created
     * @return the response data transfer object containing the details of the
     *         created promotion
     */
    @PostMapping("/promotions")
    public PromotionResponseDto createPromotion(@Valid @RequestBody PromotionRequestDto request) {
        // Create the promotion
        Promotion promotion = promotionService.createPromotion(
                request.getDescription(),
                request.getDiscountRate(),
                request.getStartLocalDate(),
                request.getEndLocalDate(),
                request.getManagerEmail(),
                request.getGameIds());
        return new PromotionResponseDto(promotion);
    }

    /**
     * Get a promotion by ID.
     * 
     * @param pid the ID of the promotion to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved promotion
     */
    @GetMapping("/promotions/{pid}")
    public PromotionResponseDto getPromotionById(@PathVariable int pid) {
        // Get the promotion by its id
        Promotion promotion = promotionService.getPromotionById(pid);
        return new PromotionResponseDto(promotion);
    }

    /**
     * Get all promotions.
     * 
     * @return a list data transfer object containing the summaries of all
     *         promotions
     */
    @GetMapping("/promotions")
    public PromotionListDto getAllPromotions() {
        List<PromotionSummaryDto> dtos = new ArrayList<>();

        // Get all promotions and add them to the list
        for (Promotion promotion : promotionService.getAllPromotions()) {
            dtos.add(new PromotionSummaryDto(promotion));
        }
        return new PromotionListDto(dtos);
    }

    /**
     * Update a promotion.
     * 
     * @param id      the ID of the promotion to update
     * @param request the promotion request data transfer object containing the
     *                updated details of the promotion
     * @return the response data transfer object containing the details of the
     *         updated promotion
     */
    @PutMapping("/promotions/{id}")
    public PromotionResponseDto updatePromotion(@PathVariable int id, @RequestBody PromotionRequestDto request) {
        // Update the promotion with the new details
        Promotion promotion = promotionService.updatePromotion(
                id,
                request.getDescription(),
                request.getDiscountRate(),
                request.getStartLocalDate(),
                request.getEndLocalDate(),
                request.getGameIds());
        return new PromotionResponseDto(promotion);
    }

    /**
     * Delete a promotion.
     * 
     * @param promotionId the ID of the promotion to delete
     */
    @DeleteMapping("/promotions/{promotionId}")
    public void deletePromotion(@PathVariable int promotionId) {
        promotionService.deletePromotion(promotionId);
    }

    /**
     * Get all games from a promotion.
     * 
     * @param promotionId the ID of the promotion to retrieve games from
     * @return a list of game summary data transfer objects containing the details
     *         of the games in the promotion
     */
    @GetMapping("/promotions/{promotionId}/games")
    public List<GameSummaryDto> getAllGamesFromPromotion(@PathVariable int promotionId) {
        List<GameSummaryDto> gameDtos = new ArrayList<>();

        // Get all games from the promotion and add them to the list
        for (Game game : promotionService.getAllGamesFromPromotion(promotionId)) {
            gameDtos.add(new GameSummaryDto(game));
        }
        return gameDtos;
    }

    /**
     * Get a specific game from a promotion.
     * 
     * @param promotionId the ID of the promotion to retrieve the game from
     * @param gameId      the ID of the game to retrieve
     * @return the game summary data transfer object containing the details of the
     *         retrieved game
     */
    @GetMapping("/promotions/{promotionId}/games/{gameId}")
    public GameSummaryDto getGameByIdFromPromotion(@PathVariable int promotionId, @PathVariable int gameId) {
        Game game = promotionService.getGameByIdFromPromotion(promotionId, gameId);
        return new GameSummaryDto(game);
    }

    /**
     * Add a game to a promotion.
     * 
     * @param promotionId the ID of the promotion to add the game to
     * @param gameId      the ID of the game to add to the promotion
     * @return the response data transfer object containing the details of the
     *         updated promotion
     */
    @PostMapping("/promotions/{promotionId}/games/{gameId}")
    public PromotionResponseDto addGameToPromotion(@PathVariable int promotionId, @PathVariable int gameId) {
        Promotion promotion = promotionService.addGameToPromotion(promotionId, gameId);
        return new PromotionResponseDto(promotion);
    }

    /**
     * Remove a game from a promotion.
     * 
     * @param promotionId the ID of the promotion to remove the game from
     * @param gameId      the ID of the game to remove from the promotion
     * @return the response data transfer object containing the details of the
     *         updated promotion
     */
    @DeleteMapping("/promotions/{promotionId}/games/{gameId}")
    public PromotionResponseDto removeGameFromPromotion(@PathVariable int promotionId, @PathVariable int gameId) {
        // Remove the game from the promotion
        Promotion promotion = promotionService.removeGameFromPromotion(promotionId, gameId);
        return new PromotionResponseDto(promotion);
    }
}