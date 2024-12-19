package com.mcgill.ecse321.GameShop.dto.ReviewDto;

import java.time.LocalDate;

import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.model.Review.GameRating;

public class ReviewResponseDto {

    private int reviewId;
    private LocalDate reviewDate;
    private String description;
    private int rating;
    private GameRating gameRating;
    private int gameId;
    private String customerEmail;

    public ReviewResponseDto() {
    }

    public ReviewResponseDto(Review review) {
        
        this.reviewId = review.getReview_id();
        this.reviewDate = review.getReviewDate();
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

    public LocalDate getReviewDate() {
        return reviewDate;
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
