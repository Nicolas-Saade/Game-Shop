package com.mcgill.ecse321.GameShop.dto.ReviewDto;

import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.model.Review.GameRating;

public class ReviewSummaryDto {

    private int reviewId;
    private int gameId;
    private String description;
    private int rating;
    private GameRating gameRating;
    private String customerEmail;

    protected ReviewSummaryDto() {
    }
    public ReviewSummaryDto(Review review) {
        this.reviewId = review.getReview_id();
        this.description = review.getDescription();
        this.rating = review.getRating();
        this.gameRating = review.getGameRating();
        this.gameId = review.getGame().getGame_id();
        this.customerEmail = review.getCustomer().getEmail();
    }

    // Getters

    public int getReviewId() {
        return reviewId;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public GameRating getGameRating() {
        return gameRating;
    }

    public int getGameId() {
        return gameId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
}
