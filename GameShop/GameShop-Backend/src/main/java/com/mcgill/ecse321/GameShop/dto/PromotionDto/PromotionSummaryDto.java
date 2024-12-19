package com.mcgill.ecse321.GameShop.dto.PromotionDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Promotion;

public class PromotionSummaryDto {

    private int promotionId;
    private String description;
    private int discountRate;
    private LocalDate startLocalDate;
    private LocalDate endLocalDate;
    private List<GameSummaryDto> games;

    public PromotionSummaryDto() {
    }

    public PromotionSummaryDto(Promotion promotion) {
        this.promotionId = promotion.getPromotion_id();
        this.description = promotion.getDescription();
        this.discountRate = promotion.getDiscountRate();
        this.startLocalDate = promotion.getStartLocalDate();
        this.endLocalDate = promotion.getEndLocalDate();
        this.games = new ArrayList<GameSummaryDto>();

        List<Game> games = promotion.getGames();

        for (Game game : games) {
            this.games.add(new GameSummaryDto(game));
        }

    }

    // Getters

    public int getPromotionId() {
        return promotionId;
    }

    public String getDescription() {
        return description;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public LocalDate getStartLocalDate() { return startLocalDate; }

    public LocalDate getEndLocalDate() { return endLocalDate; }

    public List<GameSummaryDto> getGames() {
        return games;
    }
}