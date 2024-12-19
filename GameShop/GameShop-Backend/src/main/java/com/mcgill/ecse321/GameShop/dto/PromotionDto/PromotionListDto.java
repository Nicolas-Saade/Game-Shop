package com.mcgill.ecse321.GameShop.dto.PromotionDto;

import java.util.List;

public class PromotionListDto {

    private List<PromotionSummaryDto> promotions;

    public PromotionListDto() {
    }

    public PromotionListDto(List<PromotionSummaryDto> promotions) {
        this.promotions = promotions;
    }

    // Getters and Setters

    public List<PromotionSummaryDto> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionSummaryDto> promotions) {
        this.promotions = promotions;
    }
}
