
package com.mcgill.ecse321.GameShop.integration;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformListDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformRequestDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformResponseDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.PlatformRepository;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlatformIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private PlatformRepository platformRepo;

    @Autowired
    private ManagerRepository managerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private GameRepository gameRepo;

    private int platformId;
    private static final String MANAGER_EMAIL = "managerofPlatform@example.com";
    private static final String MANAGER_PASSWORD = "managerPass";
    private static final String MANAGER_USERNAME = "managerUser";
    private static final String MANAGER_PHONE = "123-456-7890";
    private static final String MANAGER_ADDRESS = "123 Manager Street";
    private static final String PLATFORM_NAME = "PlayStation 5";
    private static final String UPDATED_PLATFORM_NAME = "Xbox Series X";
    private static final String SECOND_NAME = "Nintendo Switch";

    // Clear the database before and after all tests
    @BeforeAll
    @AfterAll
    public void clearDatabase() {
        for (Game game : gameRepo.findAll()) {
            List<Platform> platforms = new ArrayList<>(game.getPlatforms());
            for (Platform platform : platforms) {
                game.removePlatform(platform);
            }
            gameRepo.save(game);
        }

        gameRepo.deleteAll();
        platformRepo.deleteAll();
        managerRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @SuppressWarnings("null")
    @Test
    @Order(1)
    public void testCreateValidPlatform() {
        // Create a manager account
        AccountRequestDto manager = new AccountRequestDto(MANAGER_EMAIL, MANAGER_USERNAME, MANAGER_PASSWORD, MANAGER_PHONE, MANAGER_ADDRESS);
        ResponseEntity<AccountResponseDto> managerResponse = client.postForEntity("/account/manager", manager, AccountResponseDto.class);
        
        // Create a platform associated with the manager
        PlatformRequestDto request = new PlatformRequestDto(PLATFORM_NAME, MANAGER_EMAIL);
        ResponseEntity<PlatformResponseDto> response = client.postForEntity("/platforms", request, PlatformResponseDto.class);

        // Validate the manager creation response
        assertNotNull(managerResponse);
        assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
        assertEquals(MANAGER_EMAIL, managerResponse.getBody().getEmail());
        
        // Validate the platform creation response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlatformResponseDto platform = response.getBody();
        assertNotNull(platform);
        assertTrue(platform.getPlatformId() > 0);
        this.platformId = platform.getPlatformId();
        assertEquals(PLATFORM_NAME, platform.getPlatformName());
        assertEquals(MANAGER_EMAIL, platform.getManagerEmail());
    }

    @Test
    @Order(2)
    public void testGetPlatformById() {
        // Retrieve the platform by ID
        String url = String.format("/platforms/%d", this.platformId);
        ResponseEntity<PlatformResponseDto> response = client.getForEntity(url, PlatformResponseDto.class);

        // Validate the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlatformResponseDto platform = response.getBody();
        assertNotNull(platform);
        assertEquals(this.platformId, platform.getPlatformId());
        assertEquals(PLATFORM_NAME, platform.getPlatformName());
        assertEquals(MANAGER_EMAIL, platform.getManagerEmail());
    }

    @Test
    @Order(3)
    public void testUpdatePlatform() {
        // Update the platform name
        String url = String.format("/platforms/%d", this.platformId);
        PlatformRequestDto updateRequest = new PlatformRequestDto(UPDATED_PLATFORM_NAME, MANAGER_EMAIL);
        HttpEntity<PlatformRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Send the update request
        ResponseEntity<PlatformResponseDto> updateResponse = client.exchange(url, HttpMethod.PUT, requestEntity, PlatformResponseDto.class);

        // Validate the update response
        assertNotNull(updateResponse);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        PlatformResponseDto updatedPlatform = updateResponse.getBody();
        assertNotNull(updatedPlatform);
        assertEquals(this.platformId, updatedPlatform.getPlatformId());
        assertEquals(UPDATED_PLATFORM_NAME, updatedPlatform.getPlatformName());
        assertEquals(MANAGER_EMAIL, updatedPlatform.getManagerEmail());
    }

    @Test
    @Order(4)
    public void testGetAllPlatforms() {
        // Create a second platform
        PlatformRequestDto request = new PlatformRequestDto(SECOND_NAME, MANAGER_EMAIL);
        ResponseEntity<PlatformResponseDto> createResponse = client.postForEntity("/platforms", request, PlatformResponseDto.class);
        assertNotNull(createResponse);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        
        // Retrieve all platforms
        ResponseEntity<PlatformListDto> response = client.getForEntity("/platforms", PlatformListDto.class);

        // Validate the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlatformListDto platforms = response.getBody();
        assertNotNull(platforms);
        List<PlatformSummaryDto> platformList = platforms.getPlatforms();
        assertNotNull(platformList);
        assertTrue(platformList.size() >= 2);
    }

    @Test
    @Order(5)
    public void testDeletePlatform() {
        // Retrieve the platform before deletion
        String url = String.format("/platforms/%d", this.platformId);
        ResponseEntity<PlatformResponseDto> responseBefore = client.getForEntity(url, PlatformResponseDto.class);

        // Validate the platform exists before deletion
        assertNotNull(responseBefore);
        assertEquals(HttpStatus.OK, responseBefore.getStatusCode(), "Platform does not exist before deletion");
    
        // Delete the platform
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);
        assertNotNull(response);
    
        // Validate the platform no longer exists
        ResponseEntity<PlatformResponseDto> getResponse = client.getForEntity(url, PlatformResponseDto.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @SuppressWarnings("null")
    @Test
    @Order(6)
    public void testFindAllGamesInPlatform() {
        // Arrange: Create target platform and another platform
        PlatformRequestDto platformRequest = new PlatformRequestDto("Nintendo Switch", MANAGER_EMAIL);
        ResponseEntity<PlatformResponseDto> platformResponse = client.postForEntity("/platforms", platformRequest, PlatformResponseDto.class);
        assertEquals(HttpStatus.OK, platformResponse.getStatusCode(), "Platform creation failed");
        PlatformResponseDto initialPlatform = platformResponse.getBody();
        assertNotNull(initialPlatform);
        assertNotNull(platformResponse.getBody().getPlatformId());
        int targetPlatformId = platformResponse.getBody().getPlatformId();

        PlatformRequestDto otherPlatformRequest = new PlatformRequestDto("PC", MANAGER_EMAIL);
        ResponseEntity<PlatformResponseDto> otherPlatformResponse = client.postForEntity("/platforms", otherPlatformRequest, PlatformResponseDto.class);
        assertEquals(HttpStatus.OK, otherPlatformResponse.getStatusCode(), "Other platform creation failed");
        PlatformResponseDto otherPlatform = otherPlatformResponse.getBody();
        assertNotNull(otherPlatform);
        assertNotNull(otherPlatformResponse.getBody().getPlatformId());
        int otherPlatformId = otherPlatformResponse.getBody().getPlatformId();

        // Create games associated with platforms
        GameRequestDto gameRequest1 = new GameRequestDto("Zelda", "Adventure game", 60, GameStatus.InStock, 3, "zelda.com");
        gameRequest1.setPlatforms(List.of(targetPlatformId, otherPlatformId));
        ResponseEntity<GameResponseDto> gameResponse1 = client.postForEntity("/games", gameRequest1, GameResponseDto.class);
        assertEquals(HttpStatus.OK, gameResponse1.getStatusCode(), "Failed to create Game 1");
        int gameId1 = gameResponse1.getBody().getaGame_id();

        GameRequestDto gameRequest2 = new GameRequestDto("Mario", "Platform game", 50, GameStatus.InStock, 2, "mario.com");
        gameRequest2.setPlatforms(List.of(targetPlatformId));
        ResponseEntity<GameResponseDto> gameResponse2 = client.postForEntity("/games", gameRequest2, GameResponseDto.class);
        assertEquals(HttpStatus.OK, gameResponse2.getStatusCode(), "Failed to create Game 2");
        int gameId2 = gameResponse2.getBody().getaGame_id();

        GameRequestDto gameRequest3 = new GameRequestDto("Halo", "Shooter game", 70, GameStatus.InStock, 4, "halo.com");
        gameRequest3.setPlatforms(List.of(otherPlatformId));
        ResponseEntity<GameResponseDto> gameResponse3 = client.postForEntity("/games", gameRequest3, GameResponseDto.class);
        assertEquals(HttpStatus.OK, gameResponse3.getStatusCode(), "Failed to create Game 3");
        int gameId3 = gameResponse3.getBody().getaGame_id();

        // Act: Fetch all games within the target platform
        String url = String.format("/platforms/%d/games", targetPlatformId);
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        // Assert: Verify that the response contains only the games associated with the target platform
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Games not found in platform");
        GameListDto games = response.getBody();
        assertNotNull(games);
        List<GameSummaryDto> gameList = games.getGames();
        assertNotNull(gameList);

        // Verify that exactly 2 games are returned in the target platform and they match the expected game IDs
        assertEquals(2, gameList.size(), "Expected 2 games in the platform");

        List<Integer> returnedGameIds = new ArrayList<>();
        for (GameSummaryDto game : gameList) {
            returnedGameIds.add(game.getGameId());
        }
        assertTrue(returnedGameIds.contains(gameId1), "Expected to find game with ID: " + gameId1);
        assertTrue(returnedGameIds.contains(gameId2), "Expected to find game with ID: " + gameId2);
        assertFalse(returnedGameIds.contains(gameId3), "Did not expect to find game with ID: " + gameId3);
    }

    @Test
    @Order(7)
    public void testCreatePlatformMissingName() {
        // Arrange: Create a PlatformRequestDto with null platformName
        PlatformRequestDto request = new PlatformRequestDto(null, MANAGER_EMAIL);

        // Act: Attempt to create the platform
        ResponseEntity<String> response = client.postForEntity("/platforms", request, String.class);

        // Assert: Validate the response indicates a bad request
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String platform = response.getBody();
        assertNotNull(platform);
        assertTrue(platform.contains("Platform name cannot be empty"));
    }

    @Test
    @Order(8)
    public void testGetPlatformByNonExistentId() {
        // Arrange: Define a non-existent platform ID
        int nonExistentId = 9999;
        String url = String.format("/platforms/%d", nonExistentId);

        // Act: Attempt to retrieve the platform by the non-existent ID
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert: Validate the response indicates the platform does not exist
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String platform = response.getBody();
        assertNotNull(platform);
        assertTrue(platform.contains("Platform does not exist"));
    }

    @Test
    @Order(9)
    public void testUpdatePlatformInvalidId() {
        // Arrange: Define an invalid platform ID
        int invalidId = -1;
        String url = String.format("/platforms/%d", invalidId);
        PlatformRequestDto updateRequest = new PlatformRequestDto("New Platform Name", MANAGER_EMAIL);
        HttpEntity<PlatformRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act: Attempt to update the platform with the invalid ID
        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        // Assert: Validate the response indicates a bad request
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String platform = response.getBody();
        assertNotNull(platform);
        assertTrue(platform.contains("Invalid platform ID"));
    }

    @Test
    @Order(10)
    public void testGetAllPlatformsEmpty() {
        // Clear all platforms and associated games
        for (Game game : gameRepo.findAll()) {
            List<Platform> platforms = new ArrayList<>(game.getPlatforms());
            for (Platform platform : platforms) {
                game.removePlatform(platform);
            }
            gameRepo.save(game);
        }
        platformRepo.deleteAll();

        // Act: Retrieve all platforms
        ResponseEntity<PlatformListDto> response = client.getForEntity("/platforms", PlatformListDto.class);

        // Assert: Validate the response indicates no platforms exist
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PlatformListDto platforms = response.getBody();
        assertNotNull(platforms);
        List<PlatformSummaryDto> platformList = platforms.getPlatforms();
        assertNotNull(platformList);
        assertTrue(platformList.isEmpty(), "Expected no platforms in the list");
    }

    @Test
    @Order(11)
    public void testDeletePlatformNonExistentId() {
        // Arrange: Define a non-existent platform ID
        int nonExistentId = 9999;
        String url = String.format("/platforms/%d", nonExistentId);

        // Act: Attempt to delete the platform with the non-existent ID
        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        // Assert: Validate the response indicates the platform does not exist
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String platform = response.getBody();
        assertNotNull(platform);
        assertTrue(platform.contains("Platform does not exist"));
    }

    @Test
    @Order(12)
    public void testFindAllGamesInPlatformNonExistentId() {
        // Arrange: Define a non-existent platform ID
        int nonExistentId = 9999;
        String url = String.format("/platforms/%d/games", nonExistentId);

        // Act: Attempt to retrieve games for the non-existent platform ID
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert: Validate the response indicates the platform does not exist
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String platform = response.getBody();
        assertNotNull(platform);
        assertTrue(platform.contains("Platform does not exist"));
    }
}
