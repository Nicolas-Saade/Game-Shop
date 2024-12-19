package com.mcgill.ecse321.GameShop.integration;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryRequestDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryResponseDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategorySummaryDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformRequestDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformResponseDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformSummaryDto;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CategoryRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.PlatformRepository;
import com.mcgill.ecse321.GameShop.repository.SpecificGameRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class GameIntegrationTest {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private SpecificGameRepository specificGameRepository;

    private static final String GAME_TITLE = "Game Title";
    private static final String GAME_DESCRIPTION = "Game Description";
    private static final int GAME_PRICE = 50;
    private static final GameStatus GAME_STATUS = GameStatus.InStock;
    private static final int GAME_STOCK_QUANTITY = 10;
    private static final String GAME_PHOTO_URL = "https://www.exampleayre.com/game.jpg";

    private static final String MANAGER_EMAIL = "managerovjchkdhjkgfsjkghdsfaghjkfPlatformmm@example.com";
    private static final String MANAGER_PASSWORD = "managerPass";
    private static final String MANAGER_USERNAME = "managerUser";
    private static final String MANAGER_PHONE = "123-456-7890";
    private static final String MANAGER_ADDRESS = "123 Manager Street";

    private int game_id;
    private List<Integer> gameIds = new ArrayList<>();
    private List<CategorySummaryDto> categoryIds = new ArrayList<>();
    private List<PlatformSummaryDto> platformIds = new ArrayList<>();
    // private int platformId;

    @BeforeAll
    @AfterAll
    public void clearDatabase() {

        for (Category category : categoryRepository.findAll()) {
            category.removeManager();
            categoryRepository.save(category);
        }

        for (Platform platform : platformRepository.findAll()) {
            platform.removeManager();
            platformRepository.save(platform);
        }

        managerRepository.deleteAll();
        specificGameRepository.deleteAll();
        gameRepository.deleteAll();
        categoryRepository.deleteAll();
        platformRepository.deleteAll();
        accountRepo.deleteAll();
    }

    @SuppressWarnings("null")
    @Test
    @Order(1)
    public void testCreateValidGame() {
        ArrayList<Integer> platformIds = new ArrayList<>();
        ArrayList<Integer> categoryIds = new ArrayList<>();

        // Create a manager account
        AccountRequestDto manager = new AccountRequestDto(MANAGER_EMAIL, MANAGER_USERNAME, MANAGER_PASSWORD, MANAGER_PHONE, MANAGER_ADDRESS);
        ResponseEntity<AccountResponseDto> managerResponse = client.postForEntity("/account/manager", manager, AccountResponseDto.class);
        assertNotNull(managerResponse);
        assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
        assertEquals(MANAGER_EMAIL, managerResponse.getBody().getEmail());

        // Create platforms and categories
        for (int i = 1; i <= 3; i++) {
            PlatformRequestDto platformRequestDto = new PlatformRequestDto("Platform Name", MANAGER_EMAIL);
            ResponseEntity<PlatformResponseDto> platformResponse = client.postForEntity("/platforms", platformRequestDto, PlatformResponseDto.class);
            assertNotNull(platformResponse);
            assertEquals(HttpStatus.OK, platformResponse.getStatusCode());
            assertEquals("Platform Name", platformResponse.getBody().getPlatformName());
            platformIds.add(platformResponse.getBody().getPlatformId());

            CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Category Name", MANAGER_EMAIL);
            ResponseEntity<CategoryResponseDto> categoryResponse = client.postForEntity("/categories", categoryRequestDto, CategoryResponseDto.class);
            assertNotNull(categoryResponse);
            assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
            assertEquals("Category Name", categoryResponse.getBody().getCategoryName());
            categoryIds.add(categoryResponse.getBody().getCategoryId());
        }

        // Create a game with the created platforms and categories
        GameRequestDto gameRequestDto = new GameRequestDto(GAME_TITLE, GAME_DESCRIPTION, GAME_PRICE, GAME_STATUS, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        gameRequestDto.setCategories(categoryIds);
        gameRequestDto.setPlatforms(platformIds);

        // Act
        ResponseEntity<GameResponseDto> response = client.postForEntity("/games", gameRequestDto, GameResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getaGame_id() > 0, "The ID should be positive");
        GameResponseDto game = response.getBody();
        assertNotNull(game);
        this.game_id = game.getaGame_id();
        this.gameIds.add(game.getaGame_id());
        assertEquals(GAME_TITLE, response.getBody().getaTitle());
        assertEquals(GAME_DESCRIPTION, response.getBody().getaDescription());
        assertEquals(GAME_PRICE, response.getBody().getaPrice());
        assertEquals(GAME_STATUS, response.getBody().getaGameStatus());
        assertEquals(GAME_STOCK_QUANTITY, response.getBody().getaStockQuantity());
        assertEquals(GAME_PHOTO_URL, response.getBody().getaPhotoUrl());

        // Verify the categories and platforms in the response
        for (CategorySummaryDto category : response.getBody().getCategories().getCategories()) {
            assertTrue(categoryIds.contains(category.getCategoryId()));
        }

        for (PlatformSummaryDto platformInResponse : response.getBody().getPlatforms().getPlatforms()) {
            assertTrue(platformIds.contains(platformInResponse.getPlatformId()));
        }

        this.platformIds = response.getBody().getPlatforms().getPlatforms();
        this.categoryIds = response.getBody().getCategories().getCategories();

    }

    @Test
    @Order(2)
    public void testGetGameById() {
        String url = "/games/" + this.game_id;
        ResponseEntity<GameResponseDto> response = client.getForEntity(url, GameResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameResponseDto game = response.getBody();
        assertNotNull(game);
        assertEquals(this.game_id, game.getaGame_id());
        assertEquals(GAME_TITLE, game.getaTitle());
        assertEquals(GAME_DESCRIPTION, game.getaDescription());
        assertEquals(GAME_PRICE, game.getaPrice());
        assertEquals(GAME_STATUS, game.getaGameStatus());
        assertEquals(GAME_STOCK_QUANTITY, game.getaStockQuantity());
        assertEquals(GAME_PHOTO_URL, game.getaPhotoUrl());
    }

    @SuppressWarnings("null")
    @Test
    @Order(3)
    public void testGetAllGames() {

        ArrayList<Integer> platformIds = new ArrayList<>();
        ArrayList<Integer> categoryIds = new ArrayList<>();

        // Create another manager account
        AccountRequestDto manager = new AccountRequestDto(MANAGER_EMAIL + "m", MANAGER_USERNAME, MANAGER_PASSWORD, MANAGER_PHONE, MANAGER_ADDRESS);
        ResponseEntity<AccountResponseDto> managerResponse = client.postForEntity("/account/manager", manager, AccountResponseDto.class);
        assertNotNull(managerResponse);
        assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
        assertEquals(MANAGER_EMAIL + "m", managerResponse.getBody().getEmail());

        // Create more platforms and categories
        for (int i = 1; i <= 3; i++) {
            PlatformRequestDto platformRequestDto = new PlatformRequestDto("Platform Name", MANAGER_EMAIL + "m");
            ResponseEntity<PlatformResponseDto> platformResponse = client.postForEntity("/platforms", platformRequestDto, PlatformResponseDto.class);
            assertNotNull(platformResponse);
            assertEquals(HttpStatus.OK, platformResponse.getStatusCode());
            assertEquals("Platform Name", platformResponse.getBody().getPlatformName());
            platformIds.add(platformResponse.getBody().getPlatformId());

            CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Category Name", MANAGER_EMAIL + "m");
            ResponseEntity<CategoryResponseDto> categoryResponse = client.postForEntity("/categories", categoryRequestDto, CategoryResponseDto.class);
            assertNotNull(categoryResponse);
            assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
            assertEquals("Category Name", categoryResponse.getBody().getCategoryName());
            categoryIds.add(categoryResponse.getBody().getCategoryId());
        }

        // Create another game
        GameRequestDto gameRequestDto = new GameRequestDto(GAME_TITLE + "V2.0", GAME_DESCRIPTION, GAME_PRICE, GAME_STATUS, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        gameRequestDto.setCategories(categoryIds);
        gameRequestDto.setPlatforms(platformIds);

        ResponseEntity<GameResponseDto> response = client.postForEntity("/games", gameRequestDto, GameResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getaGame_id() > 0, "The ID should be positive");
        this.gameIds.add(response.getBody().getaGame_id());
        ResponseEntity<GameListDto> allGamesResponse = client.getForEntity("/games", GameListDto.class);

        GameListDto games = allGamesResponse.getBody();
        assertNotNull(games);
        assertTrue(games.getNumberOfGames() == 2, "There should be two games");

        List<GameSummaryDto> gamesList = games.getGames();
        assertEquals(HttpStatus.OK, allGamesResponse.getStatusCode());
        assertTrue(gamesList.get(1).getGameId() > 0, "The ID should be positive");

        // Verify the games in the response
        for (GameSummaryDto game : gamesList) {
            assertTrue(gameIds.contains(game.getGameId()));
        }
    }
    
    @SuppressWarnings("null")
    @Test
    @Order(4)
    public void testUpdateGame() {

        String newTitle = "New Title";
        String newDescription = "New Description";
        int newPrice = 100;
        GameStatus newStatus = GameStatus.OutOfStock;
        int newStockQuantity = 5;
        String newPhotoUrl = "https://www.example.com/newgame.jpg";

        List<Integer> platformIds = new ArrayList<>();
        List<Integer> categoryIds = new ArrayList<>();

        // Create platforms and categories for the update
        for (int i = 1; i <= 3; i++) {
            PlatformRequestDto platformRequestDto = new PlatformRequestDto("Platform Name", MANAGER_EMAIL);
            ResponseEntity<PlatformResponseDto> platformResponse = client.postForEntity("/platforms", platformRequestDto, PlatformResponseDto.class);
            assertNotNull(platformResponse);
            assertEquals(HttpStatus.OK, platformResponse.getStatusCode());
            assertEquals("Platform Name", platformResponse.getBody().getPlatformName());
            platformIds.add(platformResponse.getBody().getPlatformId());

            CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Category Name", MANAGER_EMAIL);
            ResponseEntity<CategoryResponseDto> categoryResponse = client.postForEntity("/categories", categoryRequestDto, CategoryResponseDto.class);
            assertNotNull(categoryResponse);
            assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
            assertEquals("Category Name", categoryResponse.getBody().getCategoryName());
            categoryIds.add(categoryResponse.getBody().getCategoryId());
        }

        // Update the game with new details
        HttpEntity<GameRequestDto> gameRequestDto = new HttpEntity<>(new GameRequestDto(newTitle, newDescription, newPrice, newStatus, newStockQuantity, newPhotoUrl));
        gameRequestDto.getBody().setCategories(categoryIds);
        gameRequestDto.getBody().setPlatforms(platformIds);

        ResponseEntity<GameResponseDto> updateResponse = client.exchange("/games/" + this.game_id, HttpMethod.PUT, gameRequestDto, GameResponseDto.class);

        GameResponseDto updatedGame = updateResponse.getBody();
        assertNotNull(updatedGame);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        assertTrue(updatedGame.getaGame_id() > 0, "The ID should be positive");
        
        assertEquals(newTitle, updatedGame.getaTitle());
        assertEquals(newDescription, updatedGame.getaDescription());
        assertEquals(newPrice, updatedGame.getaPrice());
        assertEquals(newStatus, updatedGame.getaGameStatus());
        assertEquals(newStockQuantity, updatedGame.getaStockQuantity());
        assertEquals(newPhotoUrl, updatedGame.getaPhotoUrl());

        // Verify the categories and platforms in the updated game
        List<Integer> returned_categoryIds = new ArrayList<>();

        for (CategorySummaryDto category : updatedGame.getCategories().getCategories()) {
            returned_categoryIds.add(category.getCategoryId());
        }

        List<Integer> returned_platformIds = new ArrayList<>();

        for (PlatformSummaryDto platform : updatedGame.getPlatforms().getPlatforms()) {
            returned_platformIds.add(platform.getPlatformId());
        }

        for (Integer categoryId : categoryIds) {
            assertTrue(returned_categoryIds.contains(categoryId));
        }
      

        for (Integer platformId : platformIds) {
            assertTrue(returned_platformIds.contains(platformId));
        }
       
        assertTrue(returned_categoryIds.containsAll(categoryIds));
        assertTrue(returned_platformIds.containsAll(platformIds));
        this.platformIds = updateResponse.getBody().getPlatforms().getPlatforms();
        this.categoryIds = updateResponse.getBody().getCategories().getCategories();

    }

    @SuppressWarnings("null")
    @Test
    @Order(5)
    public void testAddCategoryToGame() {
        // Create a new category
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Category Name", MANAGER_EMAIL);
        ResponseEntity<CategoryResponseDto> categoryResponse = client.postForEntity("/categories", categoryRequestDto, CategoryResponseDto.class);
        assertNotNull(categoryResponse);
        assertEquals(HttpStatus.OK, categoryResponse.getStatusCode());
        assertEquals("Category Name", categoryResponse.getBody().getCategoryName());

        int categoryIdToAdd = categoryResponse.getBody().getCategoryId();

        // Add the new category to the game
        ResponseEntity<GameResponseDto> response = client.exchange("/games/category/" + this.game_id + "/" + categoryIdToAdd, HttpMethod.PUT, null, GameResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameResponseDto game = response.getBody();
        assertNotNull(game);
        assertEquals(this.game_id, game.getaGame_id());

        // Verify the categories in the game
        List<Integer> returned_categoryIds = new ArrayList<>();

        for (CategorySummaryDto category : game.getCategories().getCategories()) {
            returned_categoryIds.add(category.getCategoryId());
        }

        assertTrue(returned_categoryIds.contains(categoryIdToAdd));
        assertEquals(returned_categoryIds.size(), this.categoryIds.size() + 1);

        this.categoryIds = response.getBody().getCategories().getCategories();
    }

    @SuppressWarnings("null")
    @Test
    @Order(6)
    public void testAddPlatformToGame() {
        // Create a new platform
        PlatformRequestDto platformRequestDto = new PlatformRequestDto("Platform Name", MANAGER_EMAIL);
        ResponseEntity<PlatformResponseDto> platformResponse = client.postForEntity("/platforms", platformRequestDto, PlatformResponseDto.class);
        assertNotNull(platformResponse);
        assertEquals(HttpStatus.OK, platformResponse.getStatusCode());
        assertEquals("Platform Name", platformResponse.getBody().getPlatformName());

        int platformIdToAdd = platformResponse.getBody().getPlatformId();

        // Add the new platform to the game
        ResponseEntity<GameResponseDto> response = client.exchange("/games/platform/" + this.game_id + "/" + platformIdToAdd, HttpMethod.PUT, null, GameResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameResponseDto game = response.getBody();
        assertNotNull(game);
        assertEquals(this.game_id, game.getaGame_id());

        // Verify the platforms in the game
        List<Integer> returned_platformIds = new ArrayList<>();

        for (PlatformSummaryDto platform : game.getPlatforms().getPlatforms()) {
            returned_platformIds.add(platform.getPlatformId());
        }

        assertTrue(returned_platformIds.contains(platformIdToAdd));
        assertEquals(returned_platformIds.size(), this.platformIds.size() + 1);

        this.platformIds = response.getBody().getPlatforms().getPlatforms();
    }

    @Test
    @Order(7)
    public void testGetGamesByTitle() {
        String title = "New Title";
        String url = "/games/Title/" + title;
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameListDto games = response.getBody();
        assertNotNull(games);
        assertTrue(games.getNumberOfGames() == 1, "There should be two games");

        List<GameSummaryDto> gamesList = games.getGames();
        assertTrue(gamesList.get(0).getGameId() > 0, "The ID should be positive");

        // Verify the games in the response
        for (GameSummaryDto game : gamesList) {
            assertTrue(gameIds.contains(game.getGameId()));
        }
    }

    @Test
    @Order(8)
    public void testGetGamesByStatus() {
        GameStatus status = GameStatus.OutOfStock;
        String url = "/games/Status/" + status;
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameListDto games = response.getBody();
        assertNotNull(games);
        assertTrue(games.getNumberOfGames() == 1, "There should be two games");

        List<GameSummaryDto> gamesList = games.getGames();
        assertTrue(gamesList.get(0).getGameId() > 0, "The ID should be positive");

        // Verify the games in the response
        for (GameSummaryDto game : gamesList) {
            assertTrue(gameIds.contains(game.getGameId()));
        }
    }

    @Test
    @Order(9)
    public void testGetGamesbyStockQuantity() {
        int stockQuantity = 5;
        String url = "/games/SpecificGame/" + stockQuantity;
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameListDto games = response.getBody();
        assertNotNull(games);
        assertTrue(games.getNumberOfGames() == 1, "There should be a game");

        List<GameSummaryDto> gamesList = games.getGames();
        assertTrue(gamesList.get(0).getGameId() > 0, "The ID should be positive");

        // Verify the games in the response
        for (GameSummaryDto game : gamesList) {
            assertTrue(gameIds.contains(game.getGameId()));
        }
    }

    @Test
    @Order(10)
    public void testGetGamesByInvalidStockQuantity() {
        int stockQuantity = -1;
        String url = "/games/SpecificGame/" + stockQuantity;
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(11)
    public void testGetGamesByStockQuantityNoResult() {
        int stockQuantity = 9999;
        String url = "/games/SpecificGame/" + stockQuantity;
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        GameListDto games = response.getBody();
        assertNotNull(games);
        assertNull(games.getGames(), "There should be no games");
    }

    @Test
    @Order(12)
    public void testGetGameByInvalidId() {
        ResponseEntity<GameResponseDto> response = client.getForEntity("/games/0", GameResponseDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(13)
    public void testGetGameByNonExistentId() {
        ResponseEntity<GameResponseDto> response = client.getForEntity("/games/999", GameResponseDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(14)
    public void testCreateGameWithInvalidPrice() {
        GameRequestDto gameRequestDto = new GameRequestDto(GAME_TITLE, GAME_DESCRIPTION, -1, GAME_STATUS, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> response = client.postForEntity("/games", gameRequestDto, GameResponseDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(15)
    public void testCreateGameWithInvalidTitle() {
        GameRequestDto gameRequestDto = new GameRequestDto("", GAME_DESCRIPTION, GAME_PRICE, GAME_STATUS, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> response = client.postForEntity("/games", gameRequestDto, GameResponseDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(16)
    public void testCreateGameWithInvalidStatus() {
        GameRequestDto gameRequestDto = new GameRequestDto(GAME_TITLE, GAME_DESCRIPTION, GAME_PRICE, null, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> response = client.postForEntity("/games", gameRequestDto, GameResponseDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(17)
    public void testUpdateGameWithInvalidId() {
        GameRequestDto gameRequestDto = new GameRequestDto(GAME_TITLE, GAME_DESCRIPTION, GAME_PRICE, GAME_STATUS, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> response = client.exchange("/games/9999", HttpMethod.PUT, new HttpEntity<>(gameRequestDto), GameResponseDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(18)
    public void testupdateGameWithInvalidPrice() {
        GameRequestDto gameRequestDto = new GameRequestDto(GAME_TITLE, GAME_DESCRIPTION, -1, GAME_STATUS, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> response = client.exchange("/games/" + this.game_id, HttpMethod.PUT, new HttpEntity<>(gameRequestDto), GameResponseDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(19)
    public void testupdateGameWithInvalidTitle() {
        GameRequestDto gameRequestDto = new GameRequestDto("", GAME_DESCRIPTION, GAME_PRICE, GAME_STATUS, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> response = client.exchange("/games/" + this.game_id, HttpMethod.PUT, new HttpEntity<>(gameRequestDto), GameResponseDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(20)
    public void testupdateGameWithInvalidStatus() {
        GameRequestDto gameRequestDto = new GameRequestDto(GAME_TITLE, GAME_DESCRIPTION, GAME_PRICE, null, GAME_STOCK_QUANTITY, GAME_PHOTO_URL);
        ResponseEntity<GameResponseDto> response = client.exchange("/games/" + this.game_id, HttpMethod.PUT, new HttpEntity<>(gameRequestDto), GameResponseDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(21)
    public void testAddInvalidCategoryToGame() {
        ResponseEntity<GameResponseDto> response = client.exchange("/games/category/" + this.game_id + "/9999", HttpMethod.PUT, null, GameResponseDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(22)
    public void testAddInvalidPlatformToGame() {
        ResponseEntity<GameResponseDto> response = client.exchange("/games/platform/" + this.game_id + "/9999", HttpMethod.PUT, null, GameResponseDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(23)
    public void testGetGameWithInvalidTitle() {
        ResponseEntity<GameListDto> response = client.getForEntity("/games/Title/" + " ", GameListDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(24)
    public void testGetGameWithInvalidStatus() {
        ResponseEntity<GameListDto> response = client.getForEntity("/games/Status/" + " ", GameListDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(25)
    public void testCreateSpecificGameForGame() {
        Game orginalGame = gameRepository.findById(this.game_id);
        int originalQtity = orginalGame.getStockQuantity();
        ResponseEntity<GameResponseDto> response = client.exchange("/games/specificGame/" + this.game_id + "/10", HttpMethod.PUT, null, GameResponseDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameResponseDto game = response.getBody();
        assertNotNull(game);
        assertEquals(this.game_id, game.getaGame_id());
        assertEquals(game.getaStockQuantity(), originalQtity + 10);
    }

    @SuppressWarnings("null")
    @Test
    @Order(26)
    public void testDeleteGame() {

        ResponseEntity<GameResponseDto> getResponse = client.getForEntity("/games/" + this.game_id, GameResponseDto.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());        

        ResponseEntity<Void> response = client.exchange("/games/" + this.game_id, HttpMethod.DELETE, null, Void.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        GameStatus status = GameStatus.Archived;
        ResponseEntity<GameListDto> invalidResp = client.getForEntity("/games/Status/" + status, GameListDto.class);
        assertEquals(HttpStatus.OK, invalidResp.getStatusCode());

        GameListDto games = invalidResp.getBody();
        assertEquals(games.getNumberOfGames(), 1);
    
    }

    @Test
    @Order(27)
    public void testDeleteGameWithInvalidId() {
        ResponseEntity<Void> response = client.exchange("/games/9999", HttpMethod.DELETE, null, Void.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}