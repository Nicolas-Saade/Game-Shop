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

import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountResponseDto;
import com.mcgill.ecse321.GameShop.dto.ReplyDto.ReplyListDto;
import com.mcgill.ecse321.GameShop.dto.ReplyDto.ReplySummaryDto;
import com.mcgill.ecse321.GameShop.dto.ReviewDto.ReviewListDto;
import com.mcgill.ecse321.GameShop.dto.ReviewDto.ReviewRequestDto;
import com.mcgill.ecse321.GameShop.dto.ReviewDto.ReviewResponseDto;
import com.mcgill.ecse321.GameShop.dto.ReviewDto.ReviewSummaryDto;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Reply;
import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Create a new review.
     * 
     * @param request the review request data transfer object containing the details
     *                of the review to be created
     * @return the response data transfer object containing the details of the
     *         created review
     */
    @PostMapping("/reviews")
    public ReviewResponseDto createReview(@Valid @RequestBody ReviewRequestDto request) {
        // Create the review
        Review review = reviewService.createReview(
                request.getDescription(),
                request.getGameRating(),
                request.getGameId(),
                request.getCustomerEmail());

        return new ReviewResponseDto(review);
    }

    /**
     * Get a review by ID.
     * 
     * @param id the ID of the review to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved review
     */
    @GetMapping("/reviews/review/{id}")
    public ReviewResponseDto getReviewById(@PathVariable int id) {
        // Get the review by its id
        Review review = reviewService.getReviewById(id);
        return new ReviewResponseDto(review);
    }

    /**
     * Get the customer that wrote the review.
     * 
     * @param id the ID of the review to retrieve the customer from
     * @return the account response data transfer object containing the details of
     *         the customer who wrote the review
     */
    @GetMapping("/reviews/review/customer/{id}")
    public AccountResponseDto getCustomerByReviewId(@PathVariable int id) {
        // Get the customer that wrote the review
        Customer customer = reviewService.getCustomerByReviewID(id);
        return new AccountResponseDto(customer);
    }

    /**
     * Get all the reviews for a game.
     * 
     * @param gameId the ID of the game to retrieve reviews for
     * @return a list data transfer object containing the summaries of all reviews
     *         for the specified game
     */
    @GetMapping("/reviews/review/game{gameId}")
    public ReviewListDto getReviewsByGame(@PathVariable int gameId) {
        Iterable<Review> reviews = reviewService.getReviewsByGame(gameId);
        List<ReviewSummaryDto> reviewsDto = new ArrayList<>();

        // Loop through the reviews and add the reviews to the list
        for (Review review : reviews) {
            reviewsDto.add(new ReviewSummaryDto(review));
        }
        return new ReviewListDto(reviewsDto);
    }

    /**
     * Get all the reviews done by a customer.
     * 
     * @param email the email of the customer to retrieve reviews for
     * @return a list data transfer object containing the summaries of all reviews
     *         done by the specified customer
     */
    @GetMapping("/reviews/customer/r{email}")
    public ReviewListDto getReviewsByCustomer(@PathVariable String email) {
        Iterable<Review> reviews = reviewService.getReviewsByCustomer(email);
        List<ReviewSummaryDto> reviewsDto = new ArrayList<>();

        // Loop through the reviews and add the reviews to the list
        for (Review review : reviews) {
            reviewsDto.add(new ReviewSummaryDto(review));
        }
        return new ReviewListDto(reviewsDto);
    }

    /**
     * Get the reply to a review.
     * 
     * @param reviewId the ID of the review to retrieve the reply for
     * @return the reply response data transfer object containing the details of the
     *         reply to the specified review
     */
    @GetMapping("/reviews/review/reply/{reviewId}")
    public ReplyListDto getReplyByReviewId(@PathVariable int reviewId) {
        Iterable<Reply> replies = reviewService.getReplyToReview(reviewId);
        List<ReplySummaryDto> replyDto = new ArrayList<>();
        for(Reply reply : replies) {
            replyDto.add(new ReplySummaryDto(reply));
        }
        return new ReplyListDto(replyDto);
    }

    /**
     * Update the rating of a review.
     * 
     * @param id     the ID of the review to update
     * @param rating the new rating to set for the review
     * @return the response data transfer object containing the details of the
     *         updated review
     */
    @PutMapping("/reviews/review/{id}/{rating}")
    public ReviewResponseDto updateRating(@PathVariable int id, @PathVariable int rating) {
        Review review = reviewService.updateReviewRating(id, rating);
        return new ReviewResponseDto(review);
    }
}