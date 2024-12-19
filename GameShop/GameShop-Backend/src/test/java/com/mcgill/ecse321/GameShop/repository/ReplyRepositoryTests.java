package com.mcgill.ecse321.GameShop.repository;

import java.sql.Date;
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
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Reply;
import com.mcgill.ecse321.GameShop.model.Review;
import com.mcgill.ecse321.GameShop.model.Review.GameRating;

import jakarta.transaction.Transactional;

@SpringBootTest
public class ReplyRepositoryTests {

        private static List<String> testEmails = new ArrayList<String>();

        @Autowired
        private ReplyRepository replyRepository;
        @Autowired
        private ReviewRepository reviewRepository;
        @Autowired
        private GameRepository gameRepository;
        @Autowired
        private ManagerRepository managerRepository;
        @Autowired
        private CustomerRepository customerRepository;
        @Autowired
        private EmployeeRepository employeeRepository;
        @Autowired
        private CartRepository cartRepository;

        // Clear the database before and after each test
        @BeforeEach
        @AfterEach
        public void clearDatabase() {
                replyRepository.deleteAll();
                reviewRepository.deleteAll();
                gameRepository.deleteAll();
                employeeRepository.deleteAll();
                managerRepository.deleteAll();
                customerRepository.deleteAll();
                cartRepository.deleteAll();
                Account.clearTestEmails(ReplyRepositoryTests.testEmails);
                ReplyRepositoryTests.testEmails.clear();
        }

        @Test
        @Transactional
        public void testCreateAndReadReply() {
                // Create necessary objects for the test
                String email = "tarek.elakkaoui@gmail.com";
                String username = "TarekElAkkaoui";
                String password = "pass";
                String phoneNumber = "+1 (438) 865-9298";
                String address = "0000 rue Liban";

                String title = "God of War";
                String gameDescription = "A father and son duo";
                GameStatus status = GameStatus.InStock;
                int stock = 10;
                String url = "www.";
                int price = 61;

                LocalDate reviewDate = LocalDate.parse("2024-10-09");
                int rating = 0;
                GameRating gameRating = GameRating.One;
                String reviewDescription = "This is a bad game";

                String customerEmail = "anthony.saber@hotmail.lbbb";
                String customerUsername = "AnthonySaberrr";
                String customerPassword = "pas";
                String customerPhoneNumber = "+1 (438) 865-9298";
                String customerAddress = "1234 rue Mtl";

                Date replyDate =  Date.valueOf("2024-10-10");
                String replyDescription = "Thank you";

                // Save cart and customer to the repository
                Cart cart = new Cart();
                cart = cartRepository.save(cart);

                Customer customer = new Customer(customerEmail, customerUsername, customerPassword, customerPhoneNumber,
                                customerAddress, cart);
                customer = customerRepository.save(customer);
                ReplyRepositoryTests.testEmails.add(customer.getEmail());

                // Save manager to the repository
                Manager createdManager = new Manager(email, username, password, phoneNumber, address);
                createdManager = managerRepository.save(createdManager);

                // Save game to the repository
                Game game = new Game(title, gameDescription, price, status, stock, url);
                game = gameRepository.save(game);

                // Save review to the repository
                Review review = new Review(reviewDate, reviewDescription, rating, gameRating, game, customer);
                review = reviewRepository.save(review);

                // Save reply to the repository
                Reply reply = new Reply(replyDate, replyDescription, review, createdManager);
                reply.setReviewRating(Reply.ReviewRating.Like);
                reply = replyRepository.save(reply);
                int replyId = reply.getReply_id();

                // Retrieve the reply from the repository
                Reply pulledReply = replyRepository.findById(replyId);

                // Assertions to verify the reply was saved and retrieved correctly
                assertNotNull(pulledReply);
                assertEquals(reply.getDescription(), pulledReply.getDescription());
                assertEquals(reply.getReplyDate(), pulledReply.getReplyDate());
                assertEquals(reply.getReviewRating(), pulledReply.getReviewRating());
                assertEquals(reply.getReview().getReview_id(), pulledReply.getReview().getReview_id());
                assertEquals(createdManager.getEmail(), reply.getManager().getEmail());
                assertTrue(pulledReply.getManager() instanceof Manager, "The reply account should be a manager.");
        }

        @Test
        @Transactional
        public void testCreateMultipleRepliesToReview() {
                // Create necessary objects for the test
                Cart cart2 = new Cart();
                cart2 = cartRepository.save(cart2);

                Customer customer = new Customer("101@101.com", "101", "password",
                                "+1 (101) 456-7890", "101 Rue Egypt", cart2);
                customer = customerRepository.save(customer);
                ReplyRepositoryTests.testEmails.add(customer.getEmail());

                Manager manager = new Manager("102@102.com", "102User", "pas",
                                "+1 (102) 654-3210", "102 Rue Liban");
                manager = managerRepository.save(manager);

                Game game = new Game("Batman", "I am Batman", 59, GameStatus.InStock, 101,
                                "batmanlink.com");
                game = gameRepository.save(game);

                Review review = new Review(LocalDate.parse("2024-01-10"), "I Love this Game!", 5,
                                GameRating.Five, game, customer);
                review = reviewRepository.save(review);

                // Create and save first reply
                Reply firstReply = new Reply(Date.valueOf("2023-10-02"), "Thank you for your feedback!!!",
                                review, manager);
                firstReply.setReviewRating(Reply.ReviewRating.Like);
                firstReply = replyRepository.save(firstReply);

                // Create and save second reply
                Reply secondReply = new Reply(Date.valueOf("2023-10-03"), "We appreciate your support!!!!",
                                review, manager);
                secondReply.setReviewRating(Reply.ReviewRating.Like);
                secondReply = replyRepository.save(secondReply);

                // Retrieve all replies from the repository
                Iterable<Reply> allReplies = replyRepository.findAll();

                // Filter replies associated with the specific review
                List<Reply> repliesForReview = new ArrayList<>();
                for (Reply reply : allReplies) {
                        if (reply.getReview().equals(review)
                                        && reply.getReview().getReview_id() == review.getReview_id()) {
                                repliesForReview.add(reply);
                        }
                }

                // Assertions to verify the replies were saved and retrieved correctly
                assertNotNull(repliesForReview, "Replies list should not be null.");
                assertEquals(2, repliesForReview.size(), "There should be 2 replies associated with the review.");

                // Verify that both replies are present in the retrieved list
                assertTrue(repliesForReview.contains(firstReply), "First reply should be associated with the review.");
                assertTrue(repliesForReview.contains(secondReply),
                                "Second reply should be associated with the review.");
        }
}