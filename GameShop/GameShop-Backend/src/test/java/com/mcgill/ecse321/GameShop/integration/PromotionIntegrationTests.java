package com.mcgill.ecse321.GameShop.integration;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionListDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionRequestDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionResponseDto;
import com.mcgill.ecse321.GameShop.dto.PromotionDto.PromotionSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.PromotionRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PromotionIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private PromotionRepository promotionRepo;

    private int promotionId;
    private String managerEmail = "manager_promotion@example.com";
    private List<Integer> gameIds = new ArrayList<>();
    private int gameId1;
    private int gameId2;

    @BeforeAll
    @AfterAll
    public void clearDatabase() {
        promotionRepo.deleteAll();
        gameRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidPromotion() {
        // Arrange
    
        // Create a Manager
        AccountRequestDto managerRequest = new AccountRequestDto(
            managerEmail,
            "managerUser",
            "managerPass",
            "123-456-7890",
            "123 Manager Street"
        );
        ResponseEntity<AccountResponseDto> managerResponse = client.postForEntity("/account/manager", managerRequest, AccountResponseDto.class);
        assertNotNull(managerResponse);
        assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
        AccountResponseDto manager = managerResponse.getBody();
        assertNotNull(manager);
        assertEquals(managerEmail, manager.getEmail());
    
        // Create Games
        GameRequestDto gameRequest1 = new GameRequestDto(
            "Game One",
            "First game description",
            60,
            GameStatus.InStock,
            10,
            "http://example.com/game1.jpg"
        );
        ResponseEntity<GameResponseDto> gameResponse1 = client.postForEntity("/games", gameRequest1, GameResponseDto.class);
        assertNotNull(gameResponse1);
        assertEquals(HttpStatus.OK, gameResponse1.getStatusCode());
        GameResponseDto game1 = gameResponse1.getBody();
        assertNotNull(game1);
        gameId1 = game1.getaGame_id();
    
        GameRequestDto gameRequest2 = new GameRequestDto(
            "Game Two",
            "Second game description",
            80,
            GameStatus.InStock,
            5,
            "http://example.com/game2.jpg"
        );
        ResponseEntity<GameResponseDto> gameResponse2 = client.postForEntity("/games", gameRequest2, GameResponseDto.class);
        assertNotNull(gameResponse2);
        assertEquals(HttpStatus.OK, gameResponse2.getStatusCode());
        GameResponseDto game2 = gameResponse2.getBody();
        assertNotNull(game2);
        gameId2 = game2.getaGame_id();
    
        gameIds.add(gameId1);
        gameIds.add(gameId2);
    
        // Create Promotion
        PromotionRequestDto promotionRequest = new PromotionRequestDto(
            "Fall Sale",
            20,
            LocalDate.parse("2023-10-01"),
            LocalDate.parse("2023-10-31"),
            managerEmail,
            gameIds
        );
    
        // Debugging output to verify dates
        System.out.println("Promotion Start Date in Request: " + promotionRequest.getStartLocalDate());
        System.out.println("Promotion End Date in Request: " + promotionRequest.getEndLocalDate());
    
        // Act
        ResponseEntity<PromotionResponseDto> promotionResponse = client.postForEntity("/promotions", promotionRequest, PromotionResponseDto.class);
    
        // Assert
        assertNotNull(promotionResponse);
        assertEquals(HttpStatus.OK, promotionResponse.getStatusCode());
        PromotionResponseDto promotion = promotionResponse.getBody();
        assertNotNull(promotion);
    
        // Debugging output to verify response dates
        System.out.println("Promotion Start Date in Response: " + promotion.getStartLocalDate());
        System.out.println("Promotion End Date in Response: " + promotion.getEndLocalDate());
    
        assertTrue(promotion.getPromotionId() > 0);
        promotionId = promotion.getPromotionId();
        assertEquals("Fall Sale", promotion.getDescription());
        assertEquals(20, promotion.getDiscountRate());
        assertEquals(LocalDate.parse("2023-10-01"), promotion.getStartLocalDate());  // Check if still failing here
        assertEquals(LocalDate.parse("2023-10-31"), promotion.getEndLocalDate());
        assertEquals(managerEmail, promotion.getManagerEmail());
        assertNotNull(promotion.getGames());
        assertEquals(2, promotion.getGames().size());
    }
    @Test
    @Order(2)
    public void testGetPromotionById() {
        // Arrange
        String url = String.format("/promotions/%d", promotionId);

        // Act
        ResponseEntity<PromotionResponseDto> response = client.getForEntity(url, PromotionResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PromotionResponseDto promotion = response.getBody();
        assertNotNull(promotion);
        assertEquals(promotionId, promotion.getPromotionId());
        assertEquals("Fall Sale", promotion.getDescription());
        assertEquals(20, promotion.getDiscountRate());
        assertEquals(managerEmail, promotion.getManagerEmail());
        assertNotNull(promotion.getGames());
        assertEquals(2, promotion.getGames().size());
    }

    @Test
    @Order(3)
    public void testGetAllPromotions() {
        // Arrange
        // Create another promotion
        PromotionRequestDto promotionRequest = new PromotionRequestDto(
            "Winter Sale",
            30,
            LocalDate.parse("2023-12-01"),
            LocalDate.parse("2023-12-31"),
            managerEmail,
            gameIds
        );
        ResponseEntity<PromotionResponseDto> promotionResponse = client.postForEntity("/promotions", promotionRequest, PromotionResponseDto.class);
        assertNotNull(promotionResponse);
        assertEquals(HttpStatus.OK, promotionResponse.getStatusCode());
        PromotionResponseDto promotion = promotionResponse.getBody();
        assertNotNull(promotion);
        assertTrue(promotion.getPromotionId() > 0);

        // Act
        ResponseEntity<PromotionListDto> response = client.getForEntity("/promotions", PromotionListDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PromotionListDto promotionList = response.getBody();
        assertNotNull(promotionList);
        List<PromotionSummaryDto> promotions = promotionList.getPromotions();
        assertNotNull(promotions);
        assertTrue(promotions.size() >= 2, "Expected at least 2 promotions in the list");

        // Verify that the promotions include the ones we created
        boolean foundFallSale = false;
        boolean foundWinterSale = false;
        for (PromotionSummaryDto promo : promotions) {
            if (promo.getPromotionId() == promotionId && promo.getDescription().equals("Fall Sale")) {
                foundFallSale = true;
            }
            if (promo.getDescription().equals("Winter Sale")) {
                foundWinterSale = true;
            }
        }
        assertTrue(foundFallSale, "Fall Sale promotion not found in the list");
        assertTrue(foundWinterSale, "Winter Sale promotion not found in the list");
    }

    @Test
    @Order(4)
    public void testUpdatePromotion() {
        // Arrange
        String url = String.format("/promotions/%d", promotionId);
        PromotionRequestDto updateRequest = new PromotionRequestDto();
        updateRequest.setDescription("Updated Fall Sale");
        updateRequest.setDiscountRate(25);
        updateRequest.setStartLocalDate(LocalDate.parse("2023-10-05"));
        updateRequest.setEndLocalDate(LocalDate.parse("2023-11-05"));
        updateRequest.setGameIds(gameIds); // Keeping the same games

        HttpEntity<PromotionRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act
        ResponseEntity<PromotionResponseDto> response = client.exchange(url, HttpMethod.PUT, requestEntity, PromotionResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PromotionResponseDto updatedPromotion = response.getBody();
        assertNotNull(updatedPromotion);
        assertEquals(promotionId, updatedPromotion.getPromotionId());
        assertEquals("Updated Fall Sale", updatedPromotion.getDescription());
        assertEquals(25, updatedPromotion.getDiscountRate());
        assertEquals(LocalDate.parse("2023-10-05"), updatedPromotion.getStartLocalDate());
        assertEquals(LocalDate.parse("2023-11-05"), updatedPromotion.getEndLocalDate());
    }

    @Test
    @Order(5)
    public void testDeletePromotion() {
        // Arrange
        // Create a promotion to delete
        PromotionRequestDto promotionRequest = new PromotionRequestDto(
            "Delete Sale",
            15,
            LocalDate.parse("2023-11-01"),
            LocalDate.parse("2023-11-30"),
            managerEmail,
            gameIds
        );
        ResponseEntity<PromotionResponseDto> promotionResponse = client.postForEntity("/promotions", promotionRequest, PromotionResponseDto.class);
        assertNotNull(promotionResponse);
        assertEquals(HttpStatus.OK, promotionResponse.getStatusCode());
        PromotionResponseDto promotion = promotionResponse.getBody();
        assertNotNull(promotion);
        int promotionToDeleteId = promotion.getPromotionId();

        String url = String.format("/promotions/%d", promotionToDeleteId);

        // Act
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that the promotion is deleted
        ResponseEntity<String> getResponse = client.getForEntity(url, String.class);
        assertNotNull(getResponse);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    @Order(6)
    public void testGetAllGamesFromPromotion() {
        // Arrange
        String url = String.format("/promotions/%d/games", promotionId);

        // Act
        ResponseEntity<GameSummaryDto[]> response = client.getForEntity(url, GameSummaryDto[].class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameSummaryDto[] games = response.getBody();
        assertNotNull(games);
        assertEquals(2, games.length);
    }

    @Test
    @Order(7)
    public void testGetGameByIdFromPromotion() {
        // Arrange
        String url = String.format("/promotions/%d/games/%d", promotionId, gameId1);

        // Act
        ResponseEntity<GameSummaryDto> response = client.getForEntity(url, GameSummaryDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameSummaryDto game = response.getBody();
        assertNotNull(game);
        assertEquals(gameId1, game.getGameId());
        assertEquals("Game One", game.getTitle());
    }

    @Test
    @Order(8)
    public void testAddGameToPromotion() {
        // Arrange
        // Create a new game
        GameRequestDto gameRequest = new GameRequestDto(
            "Game Three",
            "Third game description",
            70,
            GameStatus.InStock,
            15,
            "http://example.com/game3.jpg"
        );
        ResponseEntity<GameResponseDto> gameResponse = client.postForEntity("/games", gameRequest, GameResponseDto.class);
        assertNotNull(gameResponse);
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
        GameResponseDto game = gameResponse.getBody();
        assertNotNull(game);
        int newGameId = game.getaGame_id();

        String url = String.format("/promotions/%d/games/%d", promotionId, newGameId);

        // Act
        ResponseEntity<PromotionResponseDto> response = client.postForEntity(url, null, PromotionResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PromotionResponseDto updatedPromotion = response.getBody();
        assertNotNull(updatedPromotion);
        assertEquals(promotionId, updatedPromotion.getPromotionId());
        assertEquals(3, updatedPromotion.getGames().size());
    }

    @Test
    @Order(9)
    public void testRemoveGameFromPromotion() {
        // Arrange
        String url = String.format("/promotions/%d/games/%d", promotionId, gameId2);

        // Act
        ResponseEntity<PromotionResponseDto> response = client.exchange(url, HttpMethod.DELETE, null, PromotionResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PromotionResponseDto updatedPromotion = response.getBody();
        assertNotNull(updatedPromotion);
        assertEquals(promotionId, updatedPromotion.getPromotionId());
        assertEquals(2, updatedPromotion.getGames().size());
    }

    @Test
    @Order(10)
    public void testCreatePromotionInvalidData() {
        // Arrange: Missing description
        PromotionRequestDto promotionRequest = new PromotionRequestDto();
        promotionRequest.setDescription(null); // Invalid
        promotionRequest.setDiscountRate(10);
        promotionRequest.setStartLocalDate(LocalDate.parse("2023-10-01"));
        promotionRequest.setEndLocalDate(LocalDate.parse("2023-10-31"));
        promotionRequest.setManagerEmail(managerEmail);
        promotionRequest.setGameIds(gameIds);

        // Act
        ResponseEntity<String> response = client.postForEntity("/promotions", promotionRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Description cannot be empty"));
    }

    @Test
    @Order(11)
    public void testUpdatePromotionInvalidId() {
        // Arrange
        int invalidPromotionId = 9999;
        String url = String.format("/promotions/%d", invalidPromotionId);
        PromotionRequestDto updateRequest = new PromotionRequestDto();
        updateRequest.setDescription("Non-existent promotion");
        updateRequest.setDiscountRate(50);
        updateRequest.setStartLocalDate(LocalDate.parse("2023-10-01"));
        updateRequest.setEndLocalDate(LocalDate.parse("2023-10-31"));
        updateRequest.setGameIds(gameIds);

        HttpEntity<PromotionRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act
        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Promotion not found"));
    }

    @Test
    @Order(12)
    public void testDeletePromotionInvalidId() {
        // Arrange
        int invalidPromotionId = 9999;
        String url = String.format("/promotions/%d", invalidPromotionId);

        // Act
        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(13)
    public void testGetPromotionByInvalidId() {
        // Arrange
        int invalidPromotionId = 9999;
        String url = String.format("/promotions/%d", invalidPromotionId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(14)
    public void testAddGameToPromotionInvalidIds() {
        // Arrange
        int invalidPromotionId = 9999;
        int invalidGameId = 9999;
        String url = String.format("/promotions/%d/games/%d", invalidPromotionId, invalidGameId);

        // Act
        ResponseEntity<String> response = client.postForEntity(url, null, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Promotion not found") || responseBody.contains("Game not found"));
    }

    @Test
    @Order(15)
    public void testRemoveGameFromPromotionInvalidIds() {
        // Arrange
        int invalidPromotionId = 9999;
        int invalidGameId = 9999;
        String url = String.format("/promotions/%d/games/%d", invalidPromotionId, invalidGameId);

        // Act
        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Promotion not found") || responseBody.contains("Game not found"));
    }

    @Test
    @Order(16)
    public void testCreatePromotionInvalidManagerEmail() {
        // Arrange
        String invalidManagerEmail = "invalidmanager@example.com";
        PromotionRequestDto promotionRequest = new PromotionRequestDto(
            "Invalid Manager Promotion",
            20,
            LocalDate.parse("2023-10-01"),
            LocalDate.parse("2023-10-31"),
            invalidManagerEmail,
            gameIds
        );

        // Act
        ResponseEntity<String> response = client.postForEntity("/promotions", promotionRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains(String.format("Manager with email %s not found", invalidManagerEmail)));
    }

    @Test
    @Order(17)
    public void testCreatePromotionInvalidDiscountRate() {
        // Arrange: Discount rate above 100
        PromotionRequestDto promotionRequest = new PromotionRequestDto(
            "Invalid Discount Rate",
            150, // Invalid
            LocalDate.parse("2023-10-01"),
            LocalDate.parse("2023-10-31"),
            managerEmail,
            gameIds
        );

        // Act
        ResponseEntity<String> response = client.postForEntity("/promotions", promotionRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Discount rate must be between 0 and 100"));
    }

    @Test
    @Order(18)
    public void testCreatePromotionStartDateAfterEndDate() {
        // Arrange: Start date after end date
        PromotionRequestDto promotionRequest = new PromotionRequestDto(
            "Invalid Dates",
            20,
            LocalDate.parse("2023-11-01"),
            LocalDate.parse("2023-10-01"), // End date before start date
            managerEmail,
            gameIds
        );

        // Act
        ResponseEntity<String> response = client.postForEntity("/promotions", promotionRequest, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Start LocalDate cannot be after end LocalDate"));
    }

    @Test
    @Order(19)
    public void testGetGameByIdFromPromotionInvalidIds() {
        // Arrange
        int invalidPromotionId = 9999;
        int invalidGameId = 9999;
        String url = String.format("/promotions/%d/games/%d", invalidPromotionId, invalidGameId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(20)
    public void testGetAllGamesFromPromotionInvalidId() {
        // Arrange
        int invalidPromotionId = 9999;
        String url = String.format("/promotions/%d/games", invalidPromotionId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}