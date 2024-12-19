package com.mcgill.ecse321.GameShop.integration;

import java.sql.Date;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountRequestDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.ReplyDto.ReplyRequestDto;
import com.mcgill.ecse321.GameShop.dto.ReplyDto.ReplyResponseDto;
import com.mcgill.ecse321.GameShop.dto.ReviewDto.ReviewRequestDto;
import com.mcgill.ecse321.GameShop.dto.ReviewDto.ReviewResponseDto;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.Reply.ReviewRating;
import com.mcgill.ecse321.GameShop.model.Review.GameRating;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CartRepository;
import com.mcgill.ecse321.GameShop.repository.CustomerRepository;
import com.mcgill.ecse321.GameShop.repository.EmployeeRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.ReplyRepository;
import com.mcgill.ecse321.GameShop.repository.ReviewRepository;
import com.mcgill.ecse321.GameShop.repository.WishListRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReplyIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private ManagerRepository managerRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private ReplyRepository replyRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private WishListRepository wishListRepo;

    private int replyId;
    private int reviewId;
    private int gameId;
    private String customerEmail = "customer28736@example.com";
    private String managerEmail = "manager2847364@example.com";

    @BeforeAll
    @AfterAll
    public void clearDatabase() {
        replyRepo.deleteAll();
        reviewRepo.deleteAll();
        gameRepo.deleteAll();
        wishListRepo.deleteAll();
        employeeRepo.deleteAll();
        managerRepo.deleteAll();
        customerRepo.deleteAll();
        cartRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidReply() {
        // Arrange

        // Create a Game
        GameRequestDto gameRequest = new GameRequestDto(
                "Test Game",
                "Test Description",
                50,
                GameStatus.InStock,
                100,
                "http://example.com/game.jpg");
        ResponseEntity<GameResponseDto> gameResponse = client.postForEntity("/games", gameRequest,
                GameResponseDto.class);
        assertNotNull(gameResponse);
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
        GameResponseDto game = gameResponse.getBody();
        assertNotNull(game);
        gameId = game.getaGame_id();

        // Create a Customer (Cart is automatically created and associated)
        AccountRequestDto customerRequest = new AccountRequestDto(
                customerEmail,
                "customerUser",
                "customerPass",
                "123-456-7890",
                "123 Customer Street");
        ResponseEntity<AccountResponseDto> customerResponse = client.postForEntity("/account/customer", customerRequest,
                AccountResponseDto.class);
        assertNotNull(customerResponse);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        AccountResponseDto customer = customerResponse.getBody();
        assertNotNull(customer);
        assertEquals(customerEmail, customer.getEmail());

        // Create a Manager
        AccountRequestDto managerRequest = new AccountRequestDto(
                managerEmail,
                "managerUser",
                "managerPass",
                "123-456-7890",
                "123 Manager Street");
        ResponseEntity<AccountResponseDto> managerResponse = client.postForEntity("/account/manager", managerRequest,
                AccountResponseDto.class);
        assertNotNull(managerResponse);
        assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
        AccountResponseDto manager = managerResponse.getBody();
        assertNotNull(manager);
        assertEquals(managerEmail, manager.getEmail());

        // Create a Review with Game and Customer
        ReviewRequestDto reviewRequest = new ReviewRequestDto(
                "Great game!",
                GameRating.Five,
                gameId,
                customerEmail);
        ResponseEntity<ReviewResponseDto> reviewResponse = client.postForEntity("/reviews", reviewRequest,
                ReviewResponseDto.class);
        assertNotNull(reviewResponse);
        assertEquals(HttpStatus.OK, reviewResponse.getStatusCode());
        ReviewResponseDto review = reviewResponse.getBody();
        assertNotNull(review);
        reviewId = review.getReviewId();

        // Create a Reply
        ReplyRequestDto replyRequest = new ReplyRequestDto(
                Date.valueOf("2023-10-02"),
                "Thank you for your feedback!",
                ReviewRating.Like,
                reviewId,
                managerEmail);

        // Act
        ResponseEntity<ReplyResponseDto> replyResponse = client.postForEntity("/replies", replyRequest,
                ReplyResponseDto.class);

        // Assert
        assertNotNull(replyResponse);
        assertEquals(HttpStatus.OK, replyResponse.getStatusCode());
        ReplyResponseDto reply = replyResponse.getBody();
        assertNotNull(reply);
        assertTrue(reply.getReplyId() > 0);
        replyId = reply.getReplyId();
        assertEquals("Thank you for your feedback!", reply.getDescription());
        assertEquals(ReviewRating.Like, reply.getReviewRating());
        assertEquals(reviewId, reply.getReviewId());
        assertEquals(managerEmail, reply.getManagerEmail());
    }

    @Test
    @Order(2)
    public void testGetReplyById() {
        // Arrange
        String url = String.format("/replies/%d", replyId);

        // Act
        ResponseEntity<ReplyResponseDto> response = client.getForEntity(url, ReplyResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReplyResponseDto reply = response.getBody();
        assertNotNull(reply);
        assertEquals(replyId, reply.getReplyId());
        assertEquals("Thank you for your feedback!", reply.getDescription());
        assertEquals(ReviewRating.Like, reply.getReviewRating());
        assertEquals(reviewId, reply.getReviewId());
        assertEquals(managerEmail, reply.getManagerEmail());
    }

    @Test
    @Order(3)
    public void testUpdateReply() {
        // Arrange
        String url = String.format("/replies/%d", replyId);
        ReplyRequestDto updateRequest = new ReplyRequestDto();
        updateRequest.setDescription("We appreciate your feedback!");
        updateRequest.setReviewRating(ReviewRating.Like);

        HttpEntity<ReplyRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act
        ResponseEntity<ReplyResponseDto> response = client.exchange(url, HttpMethod.PUT, requestEntity,
                ReplyResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReplyResponseDto updatedReply = response.getBody();
        assertNotNull(updatedReply);
        assertEquals(replyId, updatedReply.getReplyId());
        assertEquals("We appreciate your feedback!", updatedReply.getDescription());
        assertEquals(ReviewRating.Like, updatedReply.getReviewRating());
    }

    @Test
    @Order(4)
    public void testGetReviewByReplyId() {
        // Arrange
        String url = String.format("/replies/%d/review", replyId);

        // Act
        ResponseEntity<ReviewResponseDto> response = client.getForEntity(url, ReviewResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReviewResponseDto retrievedReview = response.getBody();
        assertNotNull(retrievedReview);
        assertEquals(reviewId, retrievedReview.getReviewId());
        assertEquals("Great game!", retrievedReview.getDescription());
        assertEquals(gameId, retrievedReview.getGameId());
        assertEquals(customerEmail, retrievedReview.getCustomerEmail());
    }

    @Test
    @Order(5)
    public void testGetReplyByInvalidId() {
        // Arrange
        int invalidReplyId = 9999;
        String url = String.format("/replies/%d", invalidReplyId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Reply not found"));
    }

    @Test
    @Order(6)
    public void testUpdateReplyInvalidId() {
        // Arrange
        int invalidReplyId = 9999;
        String url = String.format("/replies/%d", invalidReplyId);
        ReplyRequestDto updateRequest = new ReplyRequestDto();
        updateRequest.setDescription("Updated description");
        updateRequest.setReviewRating(ReviewRating.Dislike);

        HttpEntity<ReplyRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act
        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Reply not found"));
    }

    @Test
    @Order(7)
    public void testGetReviewByInvalidReplyId() {
        // Arrange
        int invalidReplyId = 9999;
        String url = String.format("/replies/%d/review", invalidReplyId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Reply not found"));
    }

    @Test
    @Order(8)
    public void testCreateReplyInvalidData() {
        // Arrange: Missing description
        ReplyRequestDto replyRequest = new ReplyRequestDto();
        replyRequest.setReplyDate(Date.valueOf("2023-10-05"));
        replyRequest.setDescription(null); // Invalid
        replyRequest.setReviewRating(ReviewRating.Like);
        replyRequest.setReviewId(reviewId);
        replyRequest.setManagerEmail(managerEmail);

        // Act
        ResponseEntity<String> response = client.postForEntity("/replies", replyRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Description cannot be empty"));
    }

    @Test
    @Order(9)
    public void testUpdateReplyInvalidData() {
        // Arrange: Update with null description and null reviewRating
        String url = String.format("/replies/%d", replyId);
        ReplyRequestDto updateRequest = new ReplyRequestDto();
        updateRequest.setDescription(null);
        updateRequest.setReviewRating(null);

        HttpEntity<ReplyRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act
        ResponseEntity<ReplyResponseDto> response = client.exchange(url, HttpMethod.PUT, requestEntity,
                ReplyResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ReplyResponseDto updatedReply = response.getBody();
        assertNotNull(updatedReply);
        assertEquals(replyId, updatedReply.getReplyId());
        // The description and reviewRating should remain unchanged
        assertEquals("We appreciate your feedback!", updatedReply.getDescription());
        assertEquals(ReviewRating.Like, updatedReply.getReviewRating());
    }

    @Test
    @Order(10)
    public void testCreateReplyWithInvalidEmailFormat() {
        // Arrange
        String invalidEmail = "invalid-email-format";
        ReplyRequestDto replyRequest = new ReplyRequestDto(
                Date.valueOf("2023-10-06"),
                "Thank you!",
                ReviewRating.Like,
                reviewId,
                invalidEmail);

        // Act
        ResponseEntity<String> response = client.postForEntity("/replies", replyRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Email should be valid"));
    }

    @Test
    @Order(11)
    public void testCreateReplyWithNullReplyDate() {
        // Arrange
        ReplyRequestDto replyRequest = new ReplyRequestDto(
                null, // Null reply date
                "Thank you!",
                ReviewRating.Like,
                reviewId,
                managerEmail);

        // Act
        ResponseEntity<String> response = client.postForEntity("/replies", replyRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Reply date cannot be null"));
    }

    @Test
    @Order(13)
    public void testCreateReplyWithNullReviewId() {
        // Arrange
        ReplyRequestDto replyRequest = new ReplyRequestDto(
                Date.valueOf("2023-10-08"),
                "Thank you!",
                ReviewRating.Like,
                null, // Null review ID
                managerEmail);

        // Act
        ResponseEntity<String> response = client.postForEntity("/replies", replyRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Review ID cannot be null"));
    }

    @Test
    @Order(14)
    public void testCreateReplyWithBlankDescription() {
        // Arrange
        ReplyRequestDto replyRequest = new ReplyRequestDto(
                Date.valueOf("2023-10-09"),
                "   ", // Blank description
                ReviewRating.Like,
                reviewId,
                managerEmail);

        // Act
        ResponseEntity<String> response = client.postForEntity("/replies", replyRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Description cannot be empty"));
    }

    @Test
    @Order(15)
    public void testCreateReplyWithBlankManagerEmail() {
        // Arrange
        ReplyRequestDto replyRequest = new ReplyRequestDto(
                Date.valueOf("2023-10-10"),
                "Thank you!",
                ReviewRating.Like,
                reviewId,
                "   " // Blank manager email
        );

        // Act
        ResponseEntity<String> response = client.postForEntity("/replies", replyRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Manager email cannot be empty"));
    }
}