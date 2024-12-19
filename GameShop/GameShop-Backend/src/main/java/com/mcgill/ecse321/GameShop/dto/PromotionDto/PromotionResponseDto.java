package com.mcgill.ecse321.GameShop.dto.PromotionDto;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Promotion;

public class PromotionResponseDto {

    private int promotionId;
    private String description;
    private int discountRate;
    private LocalDate startLocalDate;
    private LocalDate endLocalDate;
    private String managerEmail;
    private List<GameSummaryDto> games;

    public PromotionResponseDto() {
    }

    public PromotionResponseDto(Promotion promotion) {
        this.promotionId = promotion.getPromotion_id();
        this.description = promotion.getDescription();
        this.discountRate = promotion.getDiscountRate();
        this.startLocalDate = promotion.getStartLocalDate();
        this.endLocalDate = promotion.getEndLocalDate();
        this.managerEmail = promotion.getManager().getEmail();
        // Convert associated games to GameSummaryDto
        this.games = promotion.getGames().stream()
                .map(GameSummaryDto::new)
                .collect(Collectors.toList());
    }

    // Getters and Setters

    public int getPromotionId() {
        return promotionId;
    }

    public String getDescription() {
        return description;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public LocalDate getStartLocalDate() {
        return startLocalDate;
    }

    public LocalDate getEndLocalDate() {
        return endLocalDate;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public List<GameSummaryDto> getGames() {
       return games;
    }
}

