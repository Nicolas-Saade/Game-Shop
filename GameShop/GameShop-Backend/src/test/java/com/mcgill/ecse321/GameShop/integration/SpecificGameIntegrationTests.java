package com.mcgill.ecse321.GameShop.integration;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameListDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameRequestDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameResponseDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.SpecificGame.ItemStatus;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.SpecificGameRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpecificGameIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private SpecificGameRepository specificGameRepo;

    private int specificGameId;
    private int gameId;
    private int specificGameId2;
    private int newGameId;

    private static final String GAME_TITLE = "Test Gameaa";
    private static final String GAME_DESCRIPTION = "A game aused for testing.";
    private static final int GAME_PRICE = 50;
    private static final GameStatus GAME_STATUS = GameStatus.InStock;
    private static final int GAME_STOCK_QUANTITY = 100;
    private static final String GAME_PHOTO_URL = "http://exampaale.com/game.jpg";

    private static final ItemStatus SPECIFIC_GAME_ITEM_STATUS = ItemStatus.Confirmed;
    private static final ItemStatus UPDATED_SPECIFIC_GAME_ITEM_STATUS = ItemStatus.Returned;

    @BeforeAll
    @AfterAll
    public void clearDatabase() {
        specificGameRepo.deleteAll();
        gameRepo.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidSpecificGame() {
        // Ensure game exists
        GameRequestDto gameRequest = new GameRequestDto(GAME_TITLE, GAME_DESCRIPTION, GAME_PRICE, GAME_STATUS,
                GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> gameResponse = client.postForEntity("/games", gameRequest,
                GameResponseDto.class);

        // Assert Game Creation
        assertNotNull(gameResponse);
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
        GameResponseDto gameRes = gameResponse.getBody();
        assertNotNull(gameRes);
        gameId = gameRes.getaGame_id();
        assertTrue(gameId > 0);

        // Arrange
        SpecificGameRequestDto request = new SpecificGameRequestDto(SPECIFIC_GAME_ITEM_STATUS, new ArrayList<>(),
                gameId);

        // Act
        ResponseEntity<SpecificGameResponseDto> response = client.postForEntity("/specificGames", request,
                SpecificGameResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SpecificGameResponseDto specificGame = response.getBody();
        assertNotNull(specificGame);
        assertTrue(specificGame.getSpecificGame_id() > 0);
        specificGameId = specificGame.getSpecificGame_id();
        assertEquals(SPECIFIC_GAME_ITEM_STATUS, specificGame.getItemStatus());
        assertEquals(gameId, specificGame.getGame_id());
    }

    @Test
    @Order(2)
    public void testFindSpecificGameById() {
        // Arrange
        String url = String.format("/specificGames/%d", specificGameId);

        // Act
        ResponseEntity<SpecificGameResponseDto> response = client.getForEntity(url, SpecificGameResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SpecificGameResponseDto specificGame = response.getBody();
        assertNotNull(specificGame);
        assertEquals(specificGameId, specificGame.getSpecificGame_id());
        assertEquals(SPECIFIC_GAME_ITEM_STATUS, specificGame.getItemStatus());
        assertEquals(gameId, specificGame.getGame_id());
    }

    @Test
    @Order(3)
    public void testUpdateSpecificGameItemStatus() {
        // Arrange
        String url = String.format("/specificGames/%d/itemStatus", specificGameId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String newStatus = String.format("\"%s\"", UPDATED_SPECIFIC_GAME_ITEM_STATUS.toString());
        HttpEntity<String> requestEntity = new HttpEntity<>(newStatus, headers);

        // Act
        ResponseEntity<SpecificGameResponseDto> response = client.exchange(url, HttpMethod.PUT, requestEntity,
                SpecificGameResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SpecificGameResponseDto updatedSpecificGame = response.getBody();
        assertNotNull(updatedSpecificGame);
        assertEquals(specificGameId, updatedSpecificGame.getSpecificGame_id());
        assertEquals(UPDATED_SPECIFIC_GAME_ITEM_STATUS, updatedSpecificGame.getItemStatus());
    }

    @SuppressWarnings("null")
    @Test
    @Order(4)
    public void testGetAllSpecificGames() {
        // Arrange
        // Create another SpecificGame
        SpecificGameRequestDto request = new SpecificGameRequestDto(SPECIFIC_GAME_ITEM_STATUS, new ArrayList<>(),
                gameId);
        ResponseEntity<SpecificGameResponseDto> createResponse = client.postForEntity("/specificGames", request,
                SpecificGameResponseDto.class);
        assertNotNull(createResponse);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        this.specificGameId2 = createResponse.getBody().getSpecificGame_id();

        // Act
        ResponseEntity<SpecificGameListDto> response = client.getForEntity("/specificGames", SpecificGameListDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SpecificGameListDto specificGames = response.getBody();
        assertNotNull(specificGames);
        List<SpecificGameSummaryDto> specificGameList = specificGames.getGames();
        assertNotNull(specificGameList);
        assertTrue(specificGameList.size() >= 2);

    }

    @Test
    @Order(5)
    public void testGetSpecificGamesByGameId() {
        // Arrange
        String url = String.format("/games/%d/specificGames", gameId);

        // Act
        ResponseEntity<SpecificGameListDto> response = client.getForEntity(url, SpecificGameListDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SpecificGameListDto specificGames = response.getBody();
        assertNotNull(specificGames);
        List<SpecificGameSummaryDto> specificGameList = specificGames.getGames();
        assertNotNull(specificGameList);
        assertTrue(specificGameList.size() >= 2, "Size: " + specificGameList.size());
        assertTrue(specificGameList.stream().anyMatch(sg -> sg.getSpecificGame_id() == specificGameId),
                "SpecificGameId: " + specificGameId);
        assertTrue(specificGameList.stream().anyMatch(sg -> sg.getSpecificGame_id() == specificGameId2),
                "SpecificGameId2: " + specificGameId2);
    }

    @Test
    @Order(6)
    public void testUpdateSpecificGameAssociatedGame() {
        // Arrange: Create a new Game to associate
        GameRequestDto newGameRequest = new GameRequestDto(
                "New Test Game",
                "A new game for testing associations.",
                70,
                GameStatus.InStock,
                50,
                "http://example.com/newgame.jpg");
        ResponseEntity<GameResponseDto> newGameResponse = client.postForEntity("/games", newGameRequest,
                GameResponseDto.class);

        // Assert New Game Creation
        assertNotNull(newGameResponse);
        assertEquals(HttpStatus.OK, newGameResponse.getStatusCode());
        GameResponseDto newGameRes = newGameResponse.getBody();
        assertNotNull(newGameRes);
        newGameId = newGameRes.getaGame_id();
        assertTrue(newGameId > 0);

        // Act: Update SpecificGame to associate with the new Game
        String url = String.format("/specificGames/%d/game/%d", specificGameId, newGameId);
        ResponseEntity<SpecificGameResponseDto> response = client.exchange(url, HttpMethod.PUT, null,
                SpecificGameResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        SpecificGameResponseDto updatedSpecificGame = response.getBody();
        assertNotNull(updatedSpecificGame);
        assertEquals(specificGameId, updatedSpecificGame.getSpecificGame_id());
        assertEquals(newGameId, updatedSpecificGame.getGame_id());
    }

    @Test
    @Order(7)
    public void testCreateSpecificGameMissingItemStatus() {

        // Arrange: Create a SpecificGameRequestDto with null itemStatus
        SpecificGameRequestDto request = new SpecificGameRequestDto(null, new ArrayList<>(), newGameId);

        // Act
        ResponseEntity<String> response = client.postForEntity("/specificGames", request, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Item status cannot be null"));
    }

    @Test
    @Order(8)
    public void testFindSpecificGameByNonExistentId() {
        // Arrange
        int nonExistentId = 9999;
        String url = String.format("/specificGames/%d", nonExistentId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("SpecificGame does not exist"));
    }

    @Test
    @Order(9)
    public void testUpdateSpecificGameItemStatusInvalidId() {
        // Arrange
        int invalidId = -1;
        String url = String.format("/specificGames/%d/itemStatus", invalidId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String newStatus = String.format("\"%s\"", UPDATED_SPECIFIC_GAME_ITEM_STATUS.toString());
        HttpEntity<String> requestEntity = new HttpEntity<>(newStatus, headers);

        // Act
        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("SpecificGame ID must be greater than 0"));
    }

    @Test
    @Order(10)
    public void testGetAllSpecificGamesEmpty() {
        // Arrange: Clear all SpecificGames
        specificGameRepo.deleteAll();
        gameRepo.deleteAll();

        // Act
        ResponseEntity<SpecificGameListDto> response = client.getForEntity("/specificGames", SpecificGameListDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        SpecificGameListDto specificGames = response.getBody();
        assertNotNull(specificGames);
        assertTrue(specificGames.getGames() == null || specificGames.getGames().isEmpty(),
                "Expected no SpecificGames to be found");

    }

    @Test
    @Order(11)
    public void testGetSpecificGamesByNonExistentGameId() {
        // Arrange
        int nonExistentGameId = 9999;
        String url = String.format("/games/%d/specificGames", nonExistentGameId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Game does not exist"));
    }

    @SuppressWarnings("null")
    @Test
    @Order(12)
    public void testUpdateSpecificGameAssociatedGameInvalid() {

        // Arrange: Create a valid SpecificGame first
        GameRequestDto newGameRequest = new GameRequestDto(
                "New Test Game for test 12",
                "A new game for testing associations.",
                70,
                GameStatus.InStock,
                50,
                "http://example.com/newgame.jpg");
        ResponseEntity<GameResponseDto> newGameResponse = client.postForEntity("/games", newGameRequest,
                GameResponseDto.class);

        // Assert New Game Creation
        assertNotNull(newGameResponse);
        assertEquals(HttpStatus.OK, newGameResponse.getStatusCode());
        GameResponseDto newGameRes = newGameResponse.getBody();
        assertNotNull(newGameRes);
        int lastNewGameId = newGameRes.getaGame_id();
        assertTrue(newGameId > 0);

        SpecificGameRequestDto specificGameRequest = new SpecificGameRequestDto(SPECIFIC_GAME_ITEM_STATUS,
                new ArrayList<>(), lastNewGameId);
        ResponseEntity<SpecificGameResponseDto> specificGameResponse = client.postForEntity("/specificGames",
                specificGameRequest, SpecificGameResponseDto.class);
        assertNotNull(specificGameResponse);
        assertEquals(HttpStatus.OK, specificGameResponse.getStatusCode(), specificGameResponse.getBody().toString());
        SpecificGameResponseDto specificGame = specificGameResponse.getBody();
        assertNotNull(specificGame);

        // Act: Attempt to associate with a non-existent Game ID
        int nonExistentGameId = 9999;
        String url = String.format("/specificGames/%d/game/%d", specificGameId, nonExistentGameId);
        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, null, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Game does not exist"));
    }

}
