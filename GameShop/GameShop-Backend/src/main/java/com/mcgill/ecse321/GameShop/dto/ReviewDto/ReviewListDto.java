package com.mcgill.ecse321.GameShop.dto.ReviewDto;

import java.util.List;

public class ReviewListDto {

    private List<ReviewSummaryDto> reviews;

    protected ReviewListDto() {
    }
    public ReviewListDto(List<ReviewSummaryDto> reviews) {
        this.reviews = reviews;
    }

    // Getters and Setters

    public List<ReviewSummaryDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewSummaryDto> reviews) {
        this.reviews = reviews;
    }
}
