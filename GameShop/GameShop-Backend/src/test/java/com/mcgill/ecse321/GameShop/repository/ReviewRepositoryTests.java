package com.mcgill.ecse321.GameShop.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.model.Review.GameRating;

import jakarta.transaction.Transactional;

@SpringBootTest
public class ReviewRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;

    // Clear the database before and after each test
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        reviewRepository.deleteAll();
        gameRepository.deleteAll();
        customerRepository.deleteAll();
        cartRepository.deleteAll();
        Account.clearTestEmails(ReviewRepositoryTests.testEmails);
        ReviewRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadReview() {
        // Create necessary objects
        String customerEmail = "tarek.elakkaoui@gmail.ca";
        String customerUsername = "TarekEl-Akkaoui";
        String customerPassword = "pass";
        String customerPhoneNumber = "+1 (438) 865-9292";
        String customerAddress = "0000 rue Liban";

        String title = "Tetris";
        String gameDescription = "A block puzzle game";
        GameStatus status = GameStatus.InStock;
        int stock = 11;
        String url = "www.randomliiiiiink.ca";
        int price = 70;

        LocalDate reviewDate = LocalDate.parse("2024-10-05");
        int rating = 0;
        GameRating gameRating = GameRating.Five;
        String reviewDescription = "This is a very good game";

        // Save cart to the repository
        Cart cart = new Cart();
        cart = cartRepository.save(cart);

        // Save customer to the repository
        Customer customer = new Customer(customerEmail, customerUsername, customerPassword, customerPhoneNumber,
                customerAddress, cart);
        customer = customerRepository.save(customer);
        ReviewRepositoryTests.testEmails.add(customer.getEmail());

        // Save game to the repository
        Game game = new Game(title, gameDescription, price, status, stock, url);
        game = gameRepository.save(game);

        // Save review to the repository
        Review review = new Review(reviewDate, reviewDescription, rating, gameRating, game, customer);
        review = reviewRepository.save(review);
        int reviewId = review.getReview_id();

        // Get Review from the DB
        Review pulledReview = reviewRepository.findById(reviewId);

        // Assertions to verify the review was saved and retrieved correctly
        assertNotNull(pulledReview);
        assertEquals(review.getDescription(), pulledReview.getDescription());
        assertEquals(review.getReviewDate(), pulledReview.getReviewDate());
        assertEquals(review.getGameRating(), pulledReview.getGameRating());
        assertEquals(review.getGame().getGame_id(), pulledReview.getGame().getGame_id());
        assertEquals(customer.getEmail(), pulledReview.getCustomer().getEmail());
        assertEquals(review.getRating(), pulledReview.getRating());
        assertTrue(review.getCustomer() instanceof Customer, "The reviewer account should be a customer.");
    }

    @Test
    @Transactional
    public void testCreateMultipleReviewsForGame() {
        // Create necessary objects
        Cart cart2 = new Cart();
        cart2 = cartRepository.save(cart2);

        Cart cart3 = new Cart();
        cart3 = cartRepository.save(cart3);

        // Save customers to the repository
        Customer customer1 = new Customer("201@201.com", "201", "password",
                "+1 (201) 201-7890", "201 rue Crescent", cart2);
        customer1 = customerRepository.save(customer1);
        ReviewRepositoryTests.testEmails.add(customer1.getEmail());

        Customer customer2 = new Customer("202@202.com", "202", "password",
                "+1 (202) 765-2022", "202 rue Bishop", cart3);
        customer2 = customerRepository.save(customer2);
        ReviewRepositoryTests.testEmails.add(customer2.getEmail());

        // Save game to the repository
        Game game = new Game("Superman", "An interesting game", 50, GameStatus.InStock,
                10, "");
        game = gameRepository.save(game);

        // Create and save first review
        LocalDate reviewDate1 = LocalDate.parse("2024-10-05");
        GameRating gameRating1 = GameRating.Five;
        String reviewDescription1 = "This is a very good game";

        Review review1 = new Review(reviewDate1, reviewDescription1, 0, gameRating1, game, customer1);
        review1 = reviewRepository.save(review1);

        // Create and save second review
        LocalDate reviewDate2 = LocalDate.parse("2024-10-06");
        GameRating gameRating2 = GameRating.Four;
        String reviewDescription2 = "Enjoyed the game a lot";

        Review review2 = new Review(reviewDate2, reviewDescription2, 0, gameRating2, game, customer2);
        review2 = reviewRepository.save(review2);

        // Retrieve all reviews
        Iterable<Review> allReviews = reviewRepository.findAll();

        // Filter reviews associated with the specific game
        List<Review> reviewsForGame = new ArrayList<>();

        // Null Assertion before adding reviews
        assertNotNull(reviewsForGame, "Reviews list should not be null.");

        for (Review review : allReviews) {
            if (review.getGame().getGame_id() == game.getGame_id()) {
                reviewsForGame.add(review);
            }
        }

        // Assertions to verify the correct number of reviews and their association with
        // the game
        assertEquals(2, reviewsForGame.size(), "There should be 2 reviews associated with the game.");

        // Verify that both reviews are present in the retrieved list
        assertTrue(reviewsForGame.contains(review1), "First review should be associated with the game.");
        assertTrue(reviewsForGame.contains(review2), "Second review should be associated with the game");
    }
}