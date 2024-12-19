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
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryListDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryRequestDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategoryResponseDto;
import com.mcgill.ecse321.GameShop.dto.CategoryDto.CategorySummaryDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformResponseDto;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CategoryRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ManagerRepository managerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private GameRepository gameRepo;

    private int categoryId;
    private static final String MANAGER_EMAIL = "manager2@example.com";
    private static final String MANAGER_PASSWORD = "managerPass";
    private static final String MANAGER_USERNAME = "managerUser";
    private static final String MANAGER_PHONE = "123-456-7890";
    private static final String MANAGER_ADDRESS = "123 Manager Street";
    private static final String CATEGORY_NAME = "Action Games";
    private static final String UPDATED_CATEGORY_NAME = "Adventure Games";
    private static final String SECOND_NAME = "Barbie Games";

    @BeforeAll
    @AfterAll
    public void clearDatabase() {
        for (Game game : gameRepo.findAll()) {
            // Create a mutable copy of the categories list
            List<Category> categories = new ArrayList<>(game.getCategories());
            for (Category category : categories) {
                game.removeCategory(category); // Remove each category from the game
            }
            gameRepo.save(game); // Save changes to the game
        }

        // Remove associations between categories and managers
        for (Category category : categoryRepo.findAll()) {
            category.removeManager(); // Set manager to null to remove foreign key reference
            categoryRepo.save(category);
        }
        gameRepo.deleteAll();
        categoryRepo.deleteAll();

        managerRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidCategory() {
        // Ensure manager exists
        AccountRequestDto manager = new AccountRequestDto(MANAGER_EMAIL, MANAGER_USERNAME, MANAGER_PASSWORD,
                MANAGER_PHONE, MANAGER_ADDRESS);
        ResponseEntity<AccountResponseDto> managerResponse = client.postForEntity("/account/manager", manager,
                AccountResponseDto.class);
        // Arrange
        CategoryRequestDto request = new CategoryRequestDto(CATEGORY_NAME, MANAGER_EMAIL);

        // Act
        ResponseEntity<CategoryResponseDto> response = client.postForEntity("/categories", request,
                CategoryResponseDto.class);

        // Assert CREATED MANAGER
        assertNotNull(managerResponse);
        assertEquals(HttpStatus.OK, managerResponse.getStatusCode());
        AccountResponseDto managerResponseDto = managerResponse.getBody();
        assertNotNull(managerResponseDto);
        assertEquals(MANAGER_EMAIL, managerResponseDto.getEmail());
        // ASSERT CREATED CATEGORY

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CategoryResponseDto category = response.getBody();
        assertNotNull(category);
        assertTrue(category.getCategoryId() > 0);
        this.categoryId = category.getCategoryId();
        assertEquals(CATEGORY_NAME, category.getCategoryName());
        assertEquals(MANAGER_EMAIL, category.getManagerEmail());
    }

    @Test
    @Order(2)
    public void testGetCategoryById() {
        // Arrange
        String url = String.format("/categories/%d", this.categoryId);
        // Act
        ResponseEntity<CategoryResponseDto> response = client.getForEntity(url, CategoryResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CategoryResponseDto category = response.getBody();
        assertNotNull(category);
        assertEquals(this.categoryId, category.getCategoryId());
        assertEquals(CATEGORY_NAME, category.getCategoryName());
        assertEquals(MANAGER_EMAIL, category.getManagerEmail());
    }

    @Test
    @Order(3)
    public void testUpdateCategory() {
        // Arrange
        String url = String.format("/categories/%d", this.categoryId);
        CategoryRequestDto updateRequest = new CategoryRequestDto(UPDATED_CATEGORY_NAME, MANAGER_EMAIL);
        HttpEntity<CategoryRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act
        ResponseEntity<CategoryResponseDto> updateResponse = client.exchange(url, HttpMethod.PUT, requestEntity,
                CategoryResponseDto.class);

        // Assert
        assertNotNull(updateResponse);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        CategoryResponseDto updatedCategory = updateResponse.getBody();
        assertNotNull(updatedCategory);
        assertEquals(this.categoryId, updatedCategory.getCategoryId());
        assertEquals(UPDATED_CATEGORY_NAME, updatedCategory.getCategoryName());
        assertEquals(MANAGER_EMAIL, updatedCategory.getManagerEmail());
    }

    @Test
    @Order(4)
    public void testGetAllCategories() {
        // Arrange
        // Create another category
        CategoryRequestDto request = new CategoryRequestDto(SECOND_NAME, MANAGER_EMAIL);
        ResponseEntity<CategoryResponseDto> createResponse = client.postForEntity("/categories", request,
                CategoryResponseDto.class);
        assertNotNull(createResponse);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        // Act
        ResponseEntity<CategoryListDto> response = client.getForEntity("/categories", CategoryListDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CategoryListDto categories = response.getBody();
        assertNotNull(categories);
        List<CategorySummaryDto> categoryList = categories.getCategories();
        assertNotNull(categoryList);
        assertTrue(categoryList.size() >= 2);
    }

    @Test
    @Order(5)
    public void testFindAllGamesInCategory() {
        // Arrange
        // Create a category with name "Action Games for all categories" and assign the
        // category ID to targetCategoryId
        CategoryRequestDto categoryRequest = new CategoryRequestDto("Action Games for all categories", MANAGER_EMAIL);
        ResponseEntity<CategoryResponseDto> categoryResponse = client.postForEntity("/categories", categoryRequest,
                CategoryResponseDto.class);
        assertEquals(HttpStatus.OK, categoryResponse.getStatusCode(), "Category creation failed");
        CategoryResponseDto initialCategory = categoryResponse.getBody();
        assertNotNull(initialCategory, "Category is null");
        assertNotNull(initialCategory.getCategoryId(), "Category ID is null");
        int targetCategoryId = initialCategory.getCategoryId(); // Assign the created category ID

        // Create another category with name "Adventure Games" and assign the category
        // ID to otherCategoryId
        CategoryRequestDto otherCategoryRequest = new CategoryRequestDto("Adventure Games", MANAGER_EMAIL);
        ResponseEntity<CategoryResponseDto> otherCategoryResponse = client.postForEntity("/categories",
                otherCategoryRequest, CategoryResponseDto.class);
        assertEquals(HttpStatus.OK, otherCategoryResponse.getStatusCode(), "Other category creation failed");
        CategoryResponseDto otherCategory = otherCategoryResponse.getBody();
        assertNotNull(otherCategory);
        int otherCategoryId = otherCategory.getCategoryId();

        String url = String.format("/categories/%d/games", targetCategoryId);

        // Create a game with title "Rambo 2", assign it to both targetCategoryId and
        // otherCategoryId
        GameRequestDto gameRequest1 = new GameRequestDto("Rambo 2", "This game is awesome", 59, GameStatus.InStock, 5,
                "www.game.com");
        gameRequest1.setCategories(List.of(targetCategoryId, otherCategoryId));
        ResponseEntity<GameResponseDto> gameResponse1 = client.postForEntity("/games", gameRequest1,
                GameResponseDto.class);
        assertEquals(HttpStatus.OK, gameResponse1.getStatusCode(), "Failed to create Game 1");
        GameResponseDto createdGame1 = gameResponse1.getBody();
        assertNotNull(createdGame1);
        int gameId1 = createdGame1.getaGame_id();

        // Create a game with title "Terminator", assign it to targetCategoryId
        GameRequestDto gameRequest2 = new GameRequestDto("Terminator", "An action-packed game", 49, GameStatus.InStock,
                3, "www.terminator.com");
        gameRequest2.setCategories(List.of(targetCategoryId));
        ResponseEntity<GameResponseDto> gameResponse2 = client.postForEntity("/games", gameRequest2,
                GameResponseDto.class);
        assertEquals(HttpStatus.OK, gameResponse2.getStatusCode(), "Failed to create Game 2");
        GameResponseDto createdGame2 = gameResponse2.getBody();
        assertNotNull(createdGame2);
        int gameId2 = createdGame2.getaGame_id();

        // Create a game with title "Adventure Time", assign it to otherCategoryId
        GameRequestDto gameRequest3 = new GameRequestDto("Adventure Time", "An adventure game", 39, GameStatus.InStock,
                7, "www.adventure.com");
        gameRequest3.setCategories(List.of(otherCategoryId));
        ResponseEntity<GameResponseDto> gameResponse3 = client.postForEntity("/games", gameRequest3,
                GameResponseDto.class);
        assertEquals(HttpStatus.OK, gameResponse3.getStatusCode(), "Failed to create Game 3");
        GameResponseDto createdGame3 = gameResponse3.getBody();
        assertNotNull(createdGame3);
        int gameId3 = createdGame3.getaGame_id();

        // Act: Fetch all games within the category
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        // Assert: Verify that the list of games includes the created game
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Games not found in category");
        GameListDto games = response.getBody();
        assertNotNull(games);
        List<GameSummaryDto> gameList = games.getGames();
        assertNotNull(gameList);

        // Verify at least one game is returned in the category and matches the expected
        // title
        assertTrue(gameList.size() >= 1, "Expected at least one game in the category");
        assertEquals(2, gameList.size(), "Expected 2 games in the category");

        assertTrue(gameList.stream().anyMatch(game -> "Rambo 2".equals(game.getTitle())),
                "Expected to find game with title 'Rambo 2' in category");
        List<Integer> returnedGameIds = new ArrayList<>();
        for (GameSummaryDto game : gameList) {
            returnedGameIds.add(game.getGameId());
        }
        assertTrue(returnedGameIds.contains(gameId1), "Expected to find game with ID: " + gameId1);
        assertTrue(returnedGameIds.contains(gameId2), "Expected to find game with ID: " + gameId2);
        assertFalse(returnedGameIds.contains(gameId3), "Did not expect to find game with ID: " + gameId3);

    }

    @SuppressWarnings("null")
    @Test
    @Order(6)
    public void testCreateCategoryMissingName() {

        // Arrange: Create a CategoryRequestDto with null categoryName
        CategoryRequestDto request = new CategoryRequestDto(null, MANAGER_EMAIL);

        // Act
        ResponseEntity<String> response = client.postForEntity("/categories", request, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Category name cannot be empty"));
    }

    @SuppressWarnings("null")
    @Test
    @Order(7)
    public void testGetCategoryByNonExistentId() {
        // Arrange
        int nonExistentId = 9999;
        String url = String.format("/categories/%d", nonExistentId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Category does not exist"));
    }

    @SuppressWarnings("null")
    @Test
    @Order(8)
    public void testUpdateCategoryInvalidId() {
        // Arrange
        int invalidId = -1;
        String url = String.format("/categories/%d", invalidId);
        CategoryRequestDto updateRequest = new CategoryRequestDto("newnameofthetest10", MANAGER_EMAIL);
        HttpEntity<CategoryRequestDto> requestEntity = new HttpEntity<>(updateRequest);

        // Act
        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid category ID"));
    }

    @Test
    @Order(9)
    public void testDeleteCategory() {
        // Arrange
        String url = String.format("/categories/%d", this.categoryId);
        ResponseEntity<PlatformResponseDto> responseBefore = client.getForEntity(url, PlatformResponseDto.class);

        // Assert
        assertNotNull(responseBefore);
        assertEquals(HttpStatus.OK, responseBefore.getStatusCode(), "Category does not exist before deletion");

        // Act
        ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertNotNull(response);

        // Verify Deletion by trying to fetch the category again
        ResponseEntity<CategoryResponseDto> getResponse = client.getForEntity(url, CategoryResponseDto.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @SuppressWarnings("null")
    @Test
    @Order(10)
    public void testDeleteCategoryInvalidInput() {
        // Arrange
        int nonExistentId = 9999;
        String url = String.format("/categories/%d", nonExistentId);

        // Act
        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Category does not exist"));
    }

    @Test
    @Order(11)
    public void testGetAllCategoriesEmpty() {
        // Arrange: Clear all categories
        for (Game game : gameRepo.findAll()) {
            // Create a mutable copy of the categories list
            List<Category> categories = new ArrayList<>(game.getCategories());
            for (Category category : categories) {
                game.removeCategory(category); // Remove each category from the game
            }
            gameRepo.save(game); // Save changes to the game
        }
        categoryRepo.deleteAll();

        // Act
        ResponseEntity<CategoryListDto> response = client.getForEntity("/categories", CategoryListDto.class);

        // Assert
        assertNotNull(response);
        // Assuming your API returns 200 OK with an empty list when no categories exist
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CategoryListDto categories = response.getBody();
        assertNotNull(categories);
        List<CategorySummaryDto> categoryList = categories.getCategories();
        assertNotNull(categoryList);
        assertTrue(categoryList.isEmpty(), "Expected no categories in the list");
    }

    @SuppressWarnings("null")
    @Test
    @Order(12)
    public void testFindAllGamesInCategoryInvalid() {
        // Arrange
        int nonExistentId = 9999;
        String url = String.format("/categories/%d/games", nonExistentId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Category does not exist"));
    }

}
