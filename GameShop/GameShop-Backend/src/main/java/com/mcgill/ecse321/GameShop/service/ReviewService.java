package com.mcgill.ecse321.GameShop.service;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Reply;
import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.model.Review.GameRating;
import com.mcgill.ecse321.GameShop.repository.CustomerRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ReplyRepository;
import com.mcgill.ecse321.GameShop.repository.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReplyRepository replyRepository;

    // Helper method to check if a string is empty or null
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Create a new Review.
     *
     * @param reviewDate    The date of the review.
     * @param description   The description of the review.
     * @param rating        The numerical rating of the review (1-5).
     * @param gameRating    The game rating enum (One to Five).
     * @param gameId        The ID of the game being reviewed.
     * @param customerEmail The email of the customer creating the review.
     * @return The created Review.
     */
    @Transactional
    public Review createReview(String description, GameRating gameRating, int gameId, String customerEmail) {
        // Validate description
        if (description == null || description.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Description cannot be empty or null");
        }
        // Validate game rating
        if (gameRating == null) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game rating cannot be null");
        }
        // Validate game ID
        if (gameId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game ID must be positive");
        }
        // Retrieve game by ID
        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, String.format("Game with ID %d not found", gameId));
        }
        // Validate customer email
        if (isEmpty(customerEmail)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Email cannot be empty or null");
        }
        // Retrieve customer by email
        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Customer with email %s not found", customerEmail));
        }
        // Set review date to current date
        LocalDate reviewDate = LocalDate.now();
        int rating = 0;
        // Create the review
        Review review = new Review(reviewDate, description, rating, gameRating, game, customer);
        // Save and return the review
        return reviewRepository.save(review);
    }

    /**
     * Retrieve a Review by its ID.
     *
     * @param reviewId The ID of the review.
     * @return The Review object.
     */
    @Transactional
    public Review getReviewById(int reviewId) {
        // Validate review ID
        if (reviewId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Review ID must be positive");
        }
        // Retrieve review by ID
        Review review = reviewRepository.findById(reviewId);
        if (review == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Review not found");
        }
        return review;
    }

    /**
     * Retrieve the Customer that wrote a Review.
     *
     * @param reviewId The ID of the review.
     * @return The Customer object.
     */
    @Transactional
    public Customer getCustomerByReviewID(int reviewId) {
        // Validate review ID
        if (reviewId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Review ID must be positive");
        }
        // Retrieve review by ID
        Review review = getReviewById(reviewId);

        return review.getCustomer();
    }

    /**
     * Retrieve all Reviews for a Game.
     *
     * @param gameId The ID of the game.
     * @return An Iterable of Review objects.
     */
    @Transactional
    public Iterable<Review> getReviewsByGame(int gameId) {
        // Retrieve game by ID
        gameRepository.findById(gameId);
        ArrayList<Review> reviews = new ArrayList<>();
        // Iterate through all reviews and filter by game ID
        for (Review review : reviewRepository.findAll()) {
            if (review.getGame().getGame_id() == gameId) {
                reviews.add(review);
            }
        }
        return reviews;
    }

    @Transactional
    public Iterable<Review> getReviewsByCustomer(String email) {
        // Validate email
        if (isEmpty(email)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Email cannot be empty or null");
        }
        // Retrieve customer by email
        customerRepository.findByEmail(email);
        ArrayList<Review> reviews = new ArrayList<>();
        // Iterate through all reviews and filter by customer email
        for (Review review : reviewRepository.findAll()) {
            if (review.getCustomer().getEmail().equals(email)) {
                reviews.add(review);
            }
        }
        // Throw exception if no reviews found
        if (reviews.isEmpty()) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "No reviews found for given customer");
        }
        return reviews;
    }

    @Transactional
    public Iterable<Reply> getReplyToReview(int reviewId) {
        // Validate review ID
        if (reviewId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Review ID must be positive");
        }
        // Retrieve review by ID
        getReviewById(reviewId);
        ArrayList<Reply> replies = new ArrayList<>();

        // Iterate through all replies and filter by review ID
        for (Reply reply : replyRepository.findAll()) {
            if (reply.getReview().getReview_id() == reviewId) {
                replies.add(reply);
                break;
            }
        }
        return replies;
    }

    @Transactional
    public Review updateReviewRating(int reviewId, int rating) {
        // Validate review ID
        if (reviewId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Review ID must be positive");
        }
        // Validate rating value
        if (rating != -1 && rating != 1) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Rating must be either 1 or -1");
        }
        // Retrieve review by ID
        Review review = getReviewById(reviewId);
        // Update review rating
        review.setRating(review.getRating() + rating);
        return reviewRepository.save(review);
    }
}