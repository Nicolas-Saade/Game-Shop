package com.mcgill.ecse321.GameShop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.repository.CategoryRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.PlatformRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class GameServiceTests {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PlatformRepository platformRepository;
    @Mock
    private ManagerRepository managerRepository;
    @InjectMocks
    private GameService gameService;

    private static int VALID_GAME_ID;
    private static int INVALID_GAME_ID = 799;
    private static int INVALID_GAME_ID2 = -1;
    private static String VALID_TITLE = "Valid Title";
    private static String VALID_DESCRIPTION = "Valid Description";
    private static int VALID_PRICE = 100;
    private static int VALID_STOCK_QUANTITY = 10;
    private static String VALID_PHOTO_URL = "Valid Photo URL";
    private static GameStatus VALID_GAME_STATUS = GameStatus.InStock;
    private static Manager VALID_MANAGER = new Manager("manageremailofgame@example.com", "usernameof maneger",
            "ManagerPassqoesd", "154142365", "montreal");

    @Test
    public void testCreateGame() {
        VALID_GAME_ID = 1;

        // Mocking the save method of gameRepository to set the game_id and return the
        // saved game
        when(gameRepository.save(any(Game.class))).thenAnswer((InvocationOnMock invocation) -> {
            Game savedGame = invocation.getArgument(0);
            savedGame.setGame_id(VALID_GAME_ID);
            return savedGame;
        });

        // Creating a game using the gameService
        Game createdGame = gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, VALID_PRICE, VALID_GAME_STATUS,
                VALID_STOCK_QUANTITY, VALID_PHOTO_URL);

        // Assertions to verify the created game
        assertNotNull(createdGame);
        assertEquals(VALID_GAME_ID, createdGame.getGame_id(), "Game ID is not the same");
        assertEquals(VALID_TITLE, createdGame.getTitle(), "Title is not the same");
        assertEquals(VALID_DESCRIPTION, createdGame.getDescription(), "Description is not the same");
        assertEquals(VALID_PRICE, createdGame.getPrice(), "Price is not the same");
        assertEquals(VALID_GAME_STATUS, createdGame.getGameStatus(), "Game status is not the same");
        assertEquals(VALID_STOCK_QUANTITY, createdGame.getStockQuantity(), "Stock quantity is not the same");
        assertEquals(VALID_PHOTO_URL, createdGame.getPhotoUrl(), "Photo URL is not the same");

        // Verifying that the save method of gameRepository was called once
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void testCreateGameNullTitle() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(null, VALID_DESCRIPTION, VALID_PRICE, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title cannot be empty or null", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameEmptyTitle() {
        // Testing createGame with empty title
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(" ", VALID_DESCRIPTION, VALID_PRICE, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title cannot be empty or null", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameNullDescription() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, null, VALID_PRICE, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Description cannot be empty or null", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameEmptyDescription() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, " ", VALID_PRICE, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Description cannot be empty or null", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameNegativePrice() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, -10, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Price cannot be negative or 0", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameZeroPrice() {
        // Assuming price cannot be zero
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, 0, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Price cannot be negative or 0", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameNullGameStatus() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, VALID_PRICE, null, VALID_STOCK_QUANTITY,
                    VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game status cannot be null", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameNegativeStockQuantity() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, VALID_PRICE, VALID_GAME_STATUS, -5, VALID_PHOTO_URL);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Stock quantity cannot be negative", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameNullPhotoUrl() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, VALID_PRICE, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    null);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Photo URL cannot be empty or null", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testCreateGameEmptyPhotoUrl() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, VALID_PRICE, VALID_GAME_STATUS, VALID_STOCK_QUANTITY,
                    " ");
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Photo URL cannot be empty or null", exception.getMessage());
        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    public void testFindGameById() {
        VALID_GAME_ID = 2;
        // Mocking the findById method of gameRepository to return a game with the
        // VALID_GAME_ID
        Game createdGame = gameService.createGame(VALID_TITLE, VALID_DESCRIPTION, VALID_PRICE, VALID_GAME_STATUS,
                VALID_STOCK_QUANTITY, VALID_PHOTO_URL);
        createdGame.setGame_id(VALID_GAME_ID);
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(createdGame);

        // Finding the game by its ID
        Game foundGame = gameService.findGameById(VALID_GAME_ID);

        // Assertions
        assertNotNull(foundGame);
        assertEquals(VALID_GAME_ID, foundGame.getGame_id());
        assertEquals(VALID_TITLE, foundGame.getTitle());
        assertEquals(VALID_DESCRIPTION, foundGame.getDescription());
        assertEquals(VALID_PRICE, foundGame.getPrice());
        assertEquals(VALID_GAME_STATUS, foundGame.getGameStatus());
        assertEquals(VALID_STOCK_QUANTITY, foundGame.getStockQuantity());
        assertEquals(VALID_PHOTO_URL, foundGame.getPhotoUrl());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    @Test
    public void testFindGameByInvalidId() {
        when(gameRepository.findById(INVALID_GAME_ID)).thenReturn(null);
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.findGameById(INVALID_GAME_ID);
        });

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 799 does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(INVALID_GAME_ID);
    }

    @Test
    public void testFindGameByInvalidIdValue() {

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.findGameById(INVALID_GAME_ID2);
        });

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game ID must be greater than 0", exception.getMessage());
        verify(gameRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testGetAllGames() {
        // Arrange
        VALID_GAME_ID = 3;
        int SECOND_GAME_ID = 4;
        Game game1 = new Game("Game Title 1", "Description 1", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game1.setGame_id(VALID_GAME_ID);
        Game game2 = new Game("Game Title 2", "Description 2", 50, Game.GameStatus.OutOfStock, 200,
                "http://example.com/image2.jpg");
        game2.setGame_id(SECOND_GAME_ID);

        List<Game> gamesArray = Arrays.asList(game1, game2);
        when(gameRepository.findAll()).thenReturn(gamesArray);

        // Act
        Iterable<Game> games = gameService.getAllGames();

        // Assert
        assertNotNull(games);
        List<Game> gameList = new ArrayList<>();
        games.forEach(gameList::add);
        assertTrue(gameList.contains(game1));
        assertTrue(gameList.contains(game2));
        assertEquals(2, gameList.size());

        // Assertions for Game 1
        assertEquals(VALID_GAME_ID, gameList.get(0).getGame_id());
        assertEquals("Game Title 1", gameList.get(0).getTitle());
        assertEquals("Description 1", gameList.get(0).getDescription());
        assertEquals(30, gameList.get(0).getPrice());
        assertEquals(Game.GameStatus.InStock, gameList.get(0).getGameStatus());
        assertEquals(100, gameList.get(0).getStockQuantity());
        assertEquals("http://example.com/image1.jpg", gameList.get(0).getPhotoUrl());

        // Assertions for Game 2
        assertEquals(SECOND_GAME_ID, gameList.get(1).getGame_id());
        assertEquals("Game Title 2", gameList.get(1).getTitle());
        assertEquals("Description 2", gameList.get(1).getDescription());
        assertEquals(50, gameList.get(1).getPrice());
        assertEquals(Game.GameStatus.OutOfStock, gameList.get(1).getGameStatus());
        assertEquals(200, gameList.get(1).getStockQuantity());
        assertEquals("http://example.com/image2.jpg", gameList.get(1).getPhotoUrl());

        verify(gameRepository, times(1)).findAll();
    }

    // update tests in the following section
    @Test
    public void testUpdateGameTitle() {
        VALID_GAME_ID = 5;
        // Create a game
        Game game = new Game("Old Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateGameTitle(VALID_GAME_ID, "New Title");

        // Assertions
        assertNotNull(updatedGame);
        assertEquals(VALID_GAME_ID, updatedGame.getGame_id());
        assertEquals("New Title", updatedGame.getTitle());
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGameTitleWithInvalidId() {
        int invalidId = 9877;
        when(gameRepository.findById(invalidId)).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGameTitle(invalidId, "New Title");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 9877 does not exist", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(1)).findById(invalidId);
    }

    @Test
    public void testUpdateGameTitleWithNullTitle() {
        VALID_GAME_ID = 6;
        Game game = new Game("Old Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGameTitle(VALID_GAME_ID, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdateGameTitleWithEmptyTitle() {
        VALID_GAME_ID = 8;
        Game game = new Game("Old Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGameTitle(VALID_GAME_ID, "  ");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdateGameDescription() {
        VALID_GAME_ID = 9093476;
        Game game = new Game("Game Title", "Old Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateGameDescription(VALID_GAME_ID, "New Description");

        assertNotNull(updatedGame);
        assertEquals(VALID_GAME_ID, updatedGame.getGame_id());
        assertEquals("New Description", updatedGame.getDescription());
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGameDescriptionWithNullDescription() {
        VALID_GAME_ID = 10;
        Game game = new Game("Game Title", "Old Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGameDescription(VALID_GAME_ID, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Description cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdateGameDescriptionWithEmptyDescription() {
        VALID_GAME_ID = 11;
        Game game = new Game("Game Title", "Old Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGameDescription(VALID_GAME_ID, "  ");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Description cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdateGamePrice_Successful() {
        VALID_GAME_ID = 31;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateGamePrice(VALID_GAME_ID, 60);

        assertNotNull(updatedGame);
        assertEquals(VALID_GAME_ID, updatedGame.getGame_id());
        assertEquals(60, updatedGame.getPrice());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdateGamePrice_NegativePrice() {
        VALID_GAME_ID = 32;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGamePrice(VALID_GAME_ID, -20);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Price cannot be negative nor null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(0)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGamePrice_ZeroPrice() {
        VALID_GAME_ID = 35;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGamePrice(VALID_GAME_ID, 0);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Price cannot be negative nor null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdateGameStockQuantity() {
        VALID_GAME_ID = 12;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateGameStockQuantity(VALID_GAME_ID, 200);

        assertNotNull(updatedGame);
        assertEquals(VALID_GAME_ID, updatedGame.getGame_id());
        assertEquals(200, updatedGame.getStockQuantity());
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGameStockQuantityWithNegativeValue() {
        VALID_GAME_ID = 13;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGameStockQuantity(VALID_GAME_ID, -1);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Stock quantity cannot be negative", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(0)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGameStatus() {
        VALID_GAME_ID = 14;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateGameStatus(VALID_GAME_ID, Game.GameStatus.Archived);

        assertNotNull(updatedGame);
        assertEquals(VALID_GAME_ID, updatedGame.getGame_id());
        assertEquals(Game.GameStatus.Archived, updatedGame.getGameStatus());
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGameStatusWithNullStatus() {
        VALID_GAME_ID = 15;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGameStatus(VALID_GAME_ID, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game status cannot be null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(0)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGamePhotoUrl() {
        VALID_GAME_ID = 16;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/oldImage.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateGamePhotoUrl(VALID_GAME_ID, "http://example.com/newImage.jpg");

        assertNotNull(updatedGame);
        assertEquals(VALID_GAME_ID, updatedGame.getGame_id());
        assertEquals("http://example.com/newImage.jpg", updatedGame.getPhotoUrl());
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGamePhotoUrlWithNullPhotoUrl() {
        VALID_GAME_ID = 17;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/oldImage.jpg");
        game.setGame_id(VALID_GAME_ID);
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGamePhotoUrl(VALID_GAME_ID, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Photo URL cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(0)).findById(VALID_GAME_ID);
    }

    @Test
    public void testUpdateGamePhotoUrlWithEmptyPhotoUrl() {
        VALID_GAME_ID = 18;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/oldImage.jpg");
        game.setGame_id(VALID_GAME_ID);
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateGamePhotoUrl(VALID_GAME_ID, "  ");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Photo URL cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(0)).findById(VALID_GAME_ID);
    }
    /// check betweeen theeese e ttwooooo

    @Test
    public void testAddCategoryToGame_Successful() {
        VALID_GAME_ID = 19;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        Category category = new Category("Category Name", VALID_MANAGER);
        category.setCategory_id(1013);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(categoryRepository.findById(1013)).thenReturn(category);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.addCategory(VALID_GAME_ID, 1013);

        assertNotNull(updatedGame);
        assertTrue(updatedGame.getCategories().contains(category));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(categoryRepository, times(1)).findById(1013);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testAddCategoryToGame_GameNotFound() {
        VALID_GAME_ID = 20;
        Category category = new Category("Category Name", VALID_MANAGER);
        category.setCategory_id(1014);
        // when(categoryRepository.findById(1014)).thenReturn(category);
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(VALID_GAME_ID, 1014);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 20 does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
        verify(categoryRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testAddCategoryToGame_invalidGameId() {
        INVALID_GAME_ID2 = -2;
        Category category = new Category("Category Name", VALID_MANAGER);
        category.setCategory_id(10142);
        // when(categoryRepository.findById(1014)).thenReturn(category);
        // when(gameRepository.findById(INVALID_GAME_ID2)).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(INVALID_GAME_ID2, 10142);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game ID must be greater than 0", exception.getMessage());
        verify(gameRepository, times(0)).findById(INVALID_GAME_ID2);
        verify(gameRepository, never()).save(any(Game.class));
        verify(categoryRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testAddCategoryToGame_CategoryNotFound() {
        VALID_GAME_ID = 21;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);
        // when(categoryRepository.findById(1014)).thenReturn(null);
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(VALID_GAME_ID, 1014);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Category with ID 1014 does not exist", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1014);
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    public void testAddCategory_InvalidGameId() {
        int VALID_CATEGORY_ID = 22;
        Category category = new Category("Category Name", VALID_MANAGER);
        category.setCategory_id(VALID_CATEGORY_ID);
        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(category);
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(-1, VALID_CATEGORY_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game ID must be greater than 0", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).findById(anyInt());
        verify(categoryRepository, never()).findById(VALID_CATEGORY_ID);

    }

    @Test
    public void testAddCategory_InvalidCategoryId() {
        int VALID_GAME_ID = 43;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);
        // when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        int INVALID_CATEGORY_ID = -1;
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(VALID_GAME_ID, INVALID_CATEGORY_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Category ID must be greater than 0", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).findById(VALID_GAME_ID);

    }

    @Test
    public void testAddCategory_NonExistentGame() {
        VALID_GAME_ID = 44;
        int VALID_CATEGORY_ID = 44;

        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(null);

        Category category = new Category("Category Name", VALID_MANAGER);
        category.setCategory_id(VALID_CATEGORY_ID);
        // when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(category);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(VALID_GAME_ID, VALID_CATEGORY_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 44 does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(categoryRepository, never()).findById(VALID_CATEGORY_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testAddCategory_NonExistentCategory() {
        // Arrange
        VALID_GAME_ID = 45;
        int VALID_CATEGORY_ID = 45;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");

        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(VALID_GAME_ID, VALID_CATEGORY_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Category with ID 45 does not exist", exception.getMessage());

        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(categoryRepository, times(1)).findById(VALID_CATEGORY_ID);
    }

    @Test
    public void testAddCategory_CategoryAlreadyExists() {
        // Arrange
        VALID_GAME_ID = 46;
        int VALID_CATEGORY_ID = 46;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);
        Category category = new Category("Category Name", VALID_MANAGER);
        category.setCategory_id(VALID_CATEGORY_ID);
        game.addCategory(category);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(category);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addCategory(VALID_GAME_ID, VALID_CATEGORY_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Category is already in the game", exception.getMessage());

        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(categoryRepository, times(1)).findById(VALID_CATEGORY_ID);
    }

    @Test
    public void testAddPlatformToGame_Successful() {
        VALID_GAME_ID = 47;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        Platform platform = new Platform("Platform Name", VALID_MANAGER);
        platform.setPlatform_id(1013);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(platformRepository.findById(1013)).thenReturn(platform);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.addPlatform(VALID_GAME_ID, 1013);

        assertNotNull(updatedGame);
        assertTrue(updatedGame.getPlatforms().contains(platform));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(platformRepository, times(1)).findById(1013);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testAddPlatformToGame_GameNotFound() {
        VALID_GAME_ID = 48;
        Platform platform = new Platform("Platform Name", VALID_MANAGER);
        platform.setPlatform_id(1014);
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addPlatform(VALID_GAME_ID, 1014);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 48 does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
        verify(platformRepository, times(0)).findById(anyInt());
    }

    @Test
    public void testAddPlatformToGame_PlatformNotFound() {
        VALID_GAME_ID = 49;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addPlatform(VALID_GAME_ID, 1014);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Platform with ID 1014 does not exist", exception.getMessage());
        verify(platformRepository, times(1)).findById(1014);
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
    }

    @Test
    public void testAddPlatform_InvalidGameId() {
        int VALID_PLATFORM_ID = 50;
        Platform platform = new Platform("Platform Name", VALID_MANAGER);
        platform.setPlatform_id(VALID_PLATFORM_ID);
        // when(platformRepository.findById(VALID_PLATFORM_ID)).thenReturn(platform);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addPlatform(-1, VALID_PLATFORM_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game ID must be greater than 0", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).findById(anyInt());
        verify(platformRepository, never()).findById(VALID_PLATFORM_ID);
    }

    @Test
    public void testAddPlatform_InvalidPlatformId() {
        int VALID_GAME_ID = 51;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);
        int INVALID_PLATFORM_ID = -1;

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addPlatform(VALID_GAME_ID, INVALID_PLATFORM_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platform ID must be greater than 0", exception.getMessage());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameRepository, never()).findById(VALID_GAME_ID);
    }

    @Test
    public void testAddPlatform_NonExistentGame() {
        VALID_GAME_ID = 52;
        int VALID_PLATFORM_ID = 52;

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(null);

        Platform platform = new Platform("Platform Name", VALID_MANAGER);
        platform.setPlatform_id(VALID_PLATFORM_ID);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addPlatform(VALID_GAME_ID, VALID_PLATFORM_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 52 does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(platformRepository, never()).findById(VALID_PLATFORM_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testAddPlatform_NonExistentPlatform() {
        VALID_GAME_ID = 53;
        int VALID_PLATFORM_ID = 53;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");

        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(platformRepository.findById(VALID_PLATFORM_ID)).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addPlatform(VALID_GAME_ID, VALID_PLATFORM_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Platform with ID 53 does not exist", exception.getMessage());

        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(platformRepository, times(1)).findById(VALID_PLATFORM_ID);
    }

    @Test
    public void testAddPlatform_PlatformAlreadyExists() {
        VALID_GAME_ID = 54;
        int VALID_PLATFORM_ID = 54;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);
        Platform platform = new Platform("Platform Name", VALID_MANAGER);
        platform.setPlatform_id(VALID_PLATFORM_ID);
        game.addPlatform(platform);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(platformRepository.findById(VALID_PLATFORM_ID)).thenReturn(platform);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.addPlatform(VALID_GAME_ID, VALID_PLATFORM_ID);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platform is already in the game", exception.getMessage());

        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(platformRepository, times(1)).findById(VALID_PLATFORM_ID);
    }

    //// thisss iss s the line that i need to stop hierererereererewr
    @Test
    public void testUpdateCategories_Successful() {
        VALID_GAME_ID = 23;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);
        int gameCategorySize = game.getCategories().size();

        Category category1 = new Category("Category 1", VALID_MANAGER);
        category1.setCategory_id(1001);
        Category category2 = new Category("Category 2", VALID_MANAGER);
        category2.setCategory_id(1002);

        List<Integer> categoryIds = Arrays.asList(1001, 1002);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(categoryRepository.findById(1001)).thenReturn(category1);
        when(categoryRepository.findById(1002)).thenReturn(category2);

        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateCategories(game.getGame_id(), categoryIds);
        int newCategorySize = updatedGame.getCategories().size();
        int difference = newCategorySize - gameCategorySize;
        assertNotNull(updatedGame);
        assertEquals(2, difference, "Category was not added to game");
        assertTrue(updatedGame.getCategories().contains(category1));
        assertTrue(updatedGame.getCategories().contains(category2));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdateCategories_NullCategories() {
        VALID_GAME_ID = 24;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);
        // when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateCategories(VALID_GAME_ID, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Categories cannot be null", exception.getMessage());
        verify(gameRepository, never()).findById(anyInt());
        verify(categoryRepository, never()).findById(anyInt());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdateCategories_CategoryNotFound() {
        VALID_GAME_ID = 25;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        List<Integer> categoryIds = Arrays.asList(1003);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updateCategories(VALID_GAME_ID, categoryIds);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Category does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdateCategories_AddDuplicateCategories() {
        // Arrange
        VALID_GAME_ID = 26;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        Category category1 = new Category("Category 1", VALID_MANAGER);
        category1.setCategory_id(1004);

        boolean result = game.addCategory(category1);
        assertTrue(result, "Category was not added to game");

        List<Integer> categoryIds = Arrays.asList(1004);

        // Act
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(categoryRepository.findById(1004)).thenReturn(category1);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateCategories(VALID_GAME_ID, categoryIds);

        // Assert
        assertNotNull(updatedGame);
        assertEquals(1, updatedGame.getCategories().size());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(categoryRepository, times(1)).findById(1004);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdatePlatforms_Successful() {
        // Arrange
        VALID_GAME_ID = 27;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);
        int platformSize = game.getPlatforms().size();

        Platform platform1 = new Platform("Platform 1", VALID_MANAGER);
        platform1.setPlatform_id(2001);
        Platform platform2 = new Platform("Platform 2", VALID_MANAGER);
        platform2.setPlatform_id(2002);

        List<Integer> platformIds = Arrays.asList(2001, 2002);

        // Act
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(platformRepository.findById(2001)).thenReturn(platform1);
        when(platformRepository.findById(2002)).thenReturn(platform2);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updatePlatforms(VALID_GAME_ID, platformIds);
        int newPlatformSize = updatedGame.getPlatforms().size();
        int difference = newPlatformSize - platformSize;

        // Assert
        assertNotNull(updatedGame);
        assertEquals(2, difference);
        assertTrue(updatedGame.getPlatforms().contains(platform1));
        assertTrue(updatedGame.getPlatforms().contains(platform2));
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(platformRepository, times(1)).findById(2001);
        verify(platformRepository, times(1)).findById(2002);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdatePlatforms_NullPlatforms() {
        VALID_GAME_ID = 28;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);
        // when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updatePlatforms(VALID_GAME_ID, null);
        });

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platforms cannot be null", exception.getMessage());
        verify(gameRepository, never()).findById(anyInt());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdatePlatforms_PlatformNotFound() {
        VALID_GAME_ID = 29;
        List<Integer> platformIds = Arrays.asList(2003);
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.updatePlatforms(VALID_GAME_ID, platformIds);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Platform does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    public void testUpdatePlatform_empty(){
        VALID_GAME_ID = 100032;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        List<Integer> platformIds = new ArrayList<>();
        // Act
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updatePlatforms(VALID_GAME_ID, platformIds);

        // Assert
        assertNotNull(updatedGame);
        assertTrue(updatedGame.getPlatforms().isEmpty());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, times(1)).save(game);
    }
    public void testUpdateGameCategories_EmptyCategoriesList() {
        // Arrange
        VALID_GAME_ID = 10003148;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        List<Integer> categoryIds = new ArrayList<>();
        // Act
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateCategories(VALID_GAME_ID, categoryIds);
        assertNotNull(updatedGame);
        assertTrue(updatedGame.getCategories().isEmpty());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, times(1)).save(game);}

    @Test
    public void testUpdatePlatforms_AddDuplicatePlatforms() {
        // Arrange
        VALID_GAME_ID = 30;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image1.jpg");
        game.setGame_id(VALID_GAME_ID);

        Platform platform1 = new Platform("Platform 1", VALID_MANAGER);
        platform1.setPlatform_id(2004);

        boolean result = game.addPlatform(platform1);
        assertTrue(result, "Platform was not added to game");

        List<Integer> platformIds = Arrays.asList(2004);

        // Act
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(platformRepository.findById(2004)).thenReturn(platform1);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updatePlatforms(VALID_GAME_ID, platformIds);

        // Assert
        assertNotNull(updatedGame);
        assertEquals(1, updatedGame.getPlatforms().size());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(platformRepository, times(1)).findById(2004);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdateGamePlatforms_EmptyPlatforms() {
        // Arrange
        VALID_GAME_ID = 32;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        List<Integer> platformIds = new ArrayList<>();
        // Act
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updatePlatforms(VALID_GAME_ID, platformIds);

        // Assert
        assertNotNull(updatedGame);
        assertTrue(updatedGame.getPlatforms().isEmpty());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testUpdateGameCategories_EmptyCategories() {
        // Arrange
        VALID_GAME_ID = 33;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        List<Integer> categoryIds = new ArrayList<>();
        // Act
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(gameRepository.save(any(Game.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        Game updatedGame = gameService.updateCategories(VALID_GAME_ID, categoryIds);

        // Assert
        assertNotNull(updatedGame);
        assertTrue(updatedGame.getCategories().isEmpty());
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testDeleteGame_Successful() {
        VALID_GAME_ID = 34;
        Game game = new Game("Game Title", "Description", 30, Game.GameStatus.InStock, 100,
                "http://example.com/image.jpg");
        game.setGame_id(VALID_GAME_ID);

        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);

        gameService.deleteGame(VALID_GAME_ID);

        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(gameRepository, never()).delete(game);
    }

    @Test
    public void testDeleteGame_GameNotFound() {
        when(gameRepository.findById(INVALID_GAME_ID)).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.deleteGame(INVALID_GAME_ID);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 799 does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findById(INVALID_GAME_ID);
        verify(gameRepository, never()).delete(any(Game.class));
    }

    @Test
    public void testFindGamesByTitle() {
        String title = "mario cart";
        Game game1 = new Game(title, "Description 1", 50, GameStatus.InStock, 10, "http://example.com/game1.jpg");
        game1.setGame_id(35);
        Game game2 = new Game(title, "Description 2", 60, GameStatus.OutOfStock, 5, "http://example.com/game2.jpg");
        game2.setGame_id(36);

        // Add games to the list
        List<Game> games = Arrays.asList(game1, game2);
        when(gameRepository.findAllByTitle(title)).thenReturn(games);

        Iterable<Game> gameList = gameService.getGamesByTitle(title);
        List<Game> foundGames = new ArrayList<>();
        gameList.forEach(foundGames::add);

        // Assert
        assertNotNull(foundGames);
        assertEquals(2, foundGames.size());
        assertTrue(foundGames.contains(game1));
        assertTrue(foundGames.contains(game2));
        verify(gameRepository, times(1)).findAllByTitle(title);
    }

    @Test
    public void testFindGamesByTitle_EmptyTitle() {
        String title = " ";
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.getGamesByTitle(title);
        });

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).findAllByTitle(title);

    }

    @Test
    public void testFindGamesByTitle_NullTitle() {
        String title = null;
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.getGamesByTitle(title);
        });

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title cannot be empty or null", exception.getMessage());
        verify(gameRepository, never()).findAllByTitle(title);
    }

    @Test
    public void testFindGamesByTitle_notFound() {
        String title = "mario cart";
        when(gameRepository.findAllByTitle(title)).thenReturn(new ArrayList<>());

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.getGamesByTitle(title);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with title mario cart does not exist", exception.getMessage());
        verify(gameRepository, times(1)).findAllByTitle(title);
    }

    @Test
    public void testGetGamesByStatus_InvalidStatus() {
        // Arrange
        Game game1 = new Game("Game 1", "Description 1", 50, GameStatus.InStock, 10, "http://example.com/game1.jpg");
        game1.setGame_id(38);
        Game game2 = new Game("Game 2", "Description 2", 60, GameStatus.InStock, 5, "http://example.com/game2.jpg");
        game2.setGame_id(39);

        List<Game> games = Arrays.asList(game1, game2);
        when(gameRepository.findAll()).thenReturn(games);
        // Act
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.getGamesByStatus(GameStatus.OutOfStock);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No games with the given status", exception.getMessage());
        verify(gameRepository, times(1)).findAll();

    }

    @Test
    public void testGetGamesByStatus_ValidResults() {
        // Arrange
        Game game1 = new Game("Game 1", "Description 1", 50, GameStatus.InStock, 10, "http://example.com/game1.jpg");
        game1.setGame_id(40);
        Game game2 = new Game("Game 2", "Description 2", 60, GameStatus.InStock, 5, "http://example.com/game2.jpg");
        game2.setGame_id(41);

        List<Game> games = Arrays.asList(game1, game2);
        when(gameRepository.findAll()).thenReturn(games);

        // Act
        Iterable<Game> foundGames = gameService.getGamesByStatus(GameStatus.InStock);

        // Assert
        assertNotNull(foundGames);
        List<Game> gamesList = new ArrayList<>();
        foundGames.forEach(gamesList::add);

        assertEquals(2, gamesList.size());
        assertTrue(gamesList.contains(game1));
        assertTrue(gamesList.contains(game2));
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    public void testGetGamesByStatus_NullStatus() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.getGamesByStatus(null);
        });

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game status cannot be null", exception.getMessage());
        verify(gameRepository, never()).findAll();
    }

    @Test
    public void testGetGamesByStockQuantity_Valid() {
        Game game1 = new Game("Game 1", "Description 1", 50, GameStatus.InStock, 10, "http://example.com/game1.jpg");
        Game game2 = new Game("Game 2", "Description 2", 60, GameStatus.InStock, 10, "http://example.com/game2.jpg");
        Game game3 = new Game("Game 3", "Description 3", 70, GameStatus.InStock, 20, "http://example.com/game3.jpg");

        // Add games to the list
        List<Game> games = Arrays.asList(game1, game2, game3);
        when(gameRepository.findAll()).thenReturn(games);

        Iterable<Game> foundGames = gameService.getGamesByStockQuantity(10);

        // Assert
        assertNotNull(foundGames);
        List<Game> gamesList = new ArrayList<>();
        for (Game game : foundGames) {
            gamesList.add(game);
        }

        assertEquals(2, gamesList.size());
        assertTrue(gamesList.contains(game1));
        assertTrue(gamesList.contains(game2));
        assertFalse(gamesList.contains(game3));
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    public void testGetGamesByStockQuantity_NullStatus() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.getGamesByStockQuantity(-1);
        });

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Stock quantity cannot be negative", exception.getMessage());
        verify(gameRepository, never()).findAll();
    }

    @Test
    public void testGetGamesByStockQuantity_InvalidQuantity() {
        Game game1 = new Game("Game 1", "Description 1", 50, GameStatus.InStock, 10, "http://example.com/game1.jpg");
        Game game2 = new Game("Game 2", "Description 2", 60, GameStatus.InStock, 10, "http://example.com/game2.jpg");
        Game game3 = new Game("Game 3", "Description 3", 70, GameStatus.InStock, 20, "http://example.com/game3.jpg");

        // Add games to the list
        List<Game> games = Arrays.asList(game1, game2, game3);
        when(gameRepository.findAll()).thenReturn(games);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            gameService.getGamesByStockQuantity(55);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No games with the given stock quantity", exception.getMessage());
        verify(gameRepository, times(1)).findAll();
    }
}
