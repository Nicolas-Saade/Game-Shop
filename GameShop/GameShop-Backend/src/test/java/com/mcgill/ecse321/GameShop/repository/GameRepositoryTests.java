package com.mcgill.ecse321.GameShop.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;

import jakarta.transaction.Transactional;

import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.model.Promotion;
import com.mcgill.ecse321.GameShop.model.WishList;

@SpringBootTest
public class GameRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();       

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private WishListRepository wishListRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear all repositories to ensure a clean state before and after each test
        wishListRepository.deleteAll();
        promotionRepository.deleteAll();
        customerRepository.deleteAll();
        cartRepository.deleteAll();
        gameRepository.deleteAll();
        categoryRepository.deleteAll();
        platformRepository.deleteAll();
        managerRepository.deleteAll();
        Account.clearTestEmails(GameRepositoryTests.testEmails);
        GameRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadGame() {
        // Create a new game
        String title = "Super Mario";
        String description = "Mario";
        int price = 25;
        GameStatus gameStatus = GameStatus.InStock;
        int stockQuantity = 55;
        String photoUrl = "wwww.photo1.com";

        Game game = new Game(title, description, price, gameStatus, stockQuantity, photoUrl);
        game = gameRepository.save(game);

        // Retrieve the game from the database
        Game pulledGame = gameRepository.findById(game.getGame_id());

        // Create and save a manager
        String email = "11@11.com";
        String username = "T";
        String password = "pass";
        String phoneNumber = "+1 (438) 210-1111";
        String address = "1100 rue Sainte-Catherine";
        Manager createdManager = new Manager(email, username, password, phoneNumber, address);
        createdManager = managerRepository.save(createdManager);
        GameRepositoryTests.testEmails.add(createdManager.getEmail());

        // Create and save a category
        Category createdCategory = new Category("Actionssss", createdManager);
        createdCategory = categoryRepository.save(createdCategory);

        // Add category to the game and save
        game.addCategory(createdCategory);
        game = gameRepository.save(game);

        // Create and save a promotion
        LocalDate startDate = LocalDate.parse("2021-10-10");
        LocalDate endDate = LocalDate.parse("2021-10-20");
        Promotion createdPromotion = new Promotion("Promotion", 10, startDate, endDate, createdManager);
        createdPromotion.addGame(game);
        createdPromotion = promotionRepository.save(createdPromotion);
        Promotion pulledPromotion = promotionRepository.findById(createdPromotion.getPromotion_id());

        // Create and save a platform
        Platform createdPlatform = new Platform("PS5", createdManager);
        game.addPlatform(createdPlatform);
        createdPlatform = platformRepository.save(createdPlatform);
        game = gameRepository.save(game);

        // Create and save a customer with a cart
        String emailCustomer = "12@12.com";
        String usernameCustomer = "JudeSousou1";
        String passwordCustomer = "p12";
        String phoneNumberCustomer = "+1 (438) 320-239";
        String addressCustomer = "1110 rue University";
        Cart cart = new Cart();
        cart.addGame(game);
        cart = cartRepository.save(cart);
        Cart pulledCart = cartRepository.findById(cart.getCart_id());
        Customer customer1 = new Customer(emailCustomer, usernameCustomer, passwordCustomer, phoneNumberCustomer,
                addressCustomer, cart);
        GameRepositoryTests.testEmails.add(customer1.getEmail());
        customer1 = customerRepository.save(customer1);

        // Create and save a wishlist
        WishList wishlist = new WishList("title1", customer1);
        wishlist.addGame(game);
        wishlist = wishListRepository.save(wishlist);
        WishList wishListFromDb = wishListRepository.findById(wishlist.getWishList_id());

        // Assertions to verify the game added to the wishlist
        assertNotNull(wishListFromDb);
        assertEquals(title, wishListFromDb.getGames().getFirst().getTitle(), "Title should match.");
        assertEquals(description, wishListFromDb.getGames().getFirst().getDescription(), "Description should match.");
        assertEquals(price, wishListFromDb.getGames().getFirst().getPrice());
        assertEquals(gameStatus, wishListFromDb.getGames().getFirst().getGameStatus());
        assertEquals(stockQuantity, wishListFromDb.getGames().getFirst().getStockQuantity());
        assertEquals(photoUrl, wishListFromDb.getGames().getFirst().getPhotoUrl());
        assertEquals("Actionssss", wishListFromDb.getGames().getFirst().getCategories().get(0).getCategoryName());
        assertEquals("PS5", wishListFromDb.getGames().getFirst().getPlatforms().get(0).getPlatformName());

        // Assertions to verify the game added to the customer's cart
        assertEquals(emailCustomer, wishListFromDb.getCustomer().getEmail(), "Email should match.");
        assertEquals(title, wishListFromDb.getCustomer().getCart().getGames().getLast().getTitle(),
                "Title should match.");
        assertEquals(description, wishListFromDb.getCustomer().getCart().getGames().getLast().getDescription(),
                "Description should match.");
        assertEquals(1, pulledCart.getGames().size(), "Cart should have 1 game.");
        assertEquals(title, pulledCart.getGames().getLast().getTitle(), "Game title should match.");
        assertEquals(cart.getCart_id(), pulledCart.getCart_id(), "Cart ID should match.");

        // Assertions to verify the game details
        assertEquals(game.getGame_id(), pulledGame.getGame_id(), "Game ID should match.");
        assertEquals(title, pulledGame.getTitle(), "Title should match.");
        assertEquals(description, pulledGame.getDescription(), "Description should match.");
        assertEquals(price, pulledGame.getPrice(), "Price should match.");
        assertEquals(gameStatus, pulledGame.getGameStatus(), "Game status should match.");
        assertEquals(stockQuantity, pulledGame.getStockQuantity(), "Stock quantity should match.");
        assertEquals(photoUrl, pulledGame.getPhotoUrl(), "Photo URL should match.");
        assertEquals("Actionssss", pulledGame.getCategories().get(0).getCategoryName(), "Category name should match.");
        assertEquals("PS5", pulledGame.getPlatforms().get(0).getPlatformName(), "Platform name should match.");

        // Assertions to verify the promotion details
        assertNotNull(pulledPromotion);
        assertEquals(1, pulledPromotion.getGames().size(), "Promotion should have 1 game.");
        assertEquals(title, pulledPromotion.getGames().get(0).getTitle(), "Game title should match.");
        assertEquals(createdPromotion.getPromotion_id(), pulledPromotion.getPromotion_id(),
                "Promotion ID should match.");
        assertEquals(game.getGame_id(), pulledPromotion.getGames().get(0).getGame_id(), "Game ID should match.");
    }

    @Test
    @Transactional
    public void testCreateAndRetrieveGameWithMultipleCategoriesAndPlatforms() {
        // Create a new game
        String title = "tetris";
        String description = "Adventure Game";
        int price = 60;
        GameStatus gameStatus = GameStatus.InStock;
        int stockQuantity = 100;
        String photoUrl = "www.tetris.com/photo";
        Game game = new Game(title, description, price, gameStatus, stockQuantity, photoUrl);

        // Create and save a manager
        Manager manager = new Manager("13@13.com", "Manager1", "pass123", "+1 (555) 555-5555", "1234 Manager St");
        manager = managerRepository.save(manager);
        GameRepositoryTests.testEmails.add(manager.getEmail());

        // Create and save categories
        Category actionCategory = new Category("Action22", manager);
        Category adventureCategory = new Category("Adventure22", manager);
        actionCategory = categoryRepository.save(actionCategory);
        adventureCategory = categoryRepository.save(adventureCategory);

        // Create and save platforms
        Platform ps5Platform = new Platform("PS5", manager);
        Platform switchPlatform = new Platform("Switch", manager);
        ps5Platform = platformRepository.save(ps5Platform);
        switchPlatform = platformRepository.save(switchPlatform);

        // Add categories and platforms to the game and save
        game.addCategory(actionCategory);
        game.addCategory(adventureCategory);
        game.addPlatform(ps5Platform);
        game.addPlatform(switchPlatform);
        game = gameRepository.save(game);

        // Retrieve the game from the database
        Game pulledGame = gameRepository.findById(game.getGame_id());

        // Assertions to verify the game details
        assertNotNull(pulledGame);
        assertEquals(2, pulledGame.getCategories().size());
        assertEquals(2, pulledGame.getPlatforms().size());

        // Verify the categories
        int firstCategoryId = actionCategory.getCategory_id();
        boolean foundFirstCategory = pulledGame.getCategories().stream()
                .anyMatch(category -> category.getCategory_id() == firstCategoryId);
        assertTrue(foundFirstCategory, "First game should be in the cart.");
        int secondCategoryId = adventureCategory.getCategory_id();
        boolean foundSecondCategory = pulledGame.getCategories().stream()
                .anyMatch(category -> category.getCategory_id() == secondCategoryId);
        assertTrue(foundSecondCategory, "Second game should be in the cart.");

        // Verify the platforms
        int firstPlatformId = ps5Platform.getPlatform_id();
        boolean foundFirstPlatform = pulledGame.getPlatforms().stream()
                .anyMatch(platform -> platform.getPlatform_id() == firstPlatformId);
        assertTrue(foundFirstPlatform, "First platform should be in the cart.");
        int secondPlatformId = switchPlatform.getPlatform_id();
        boolean foundSecondPlatform = pulledGame.getPlatforms().stream()
                .anyMatch(platform -> platform.getPlatform_id() == secondPlatformId);
        assertTrue(foundSecondPlatform, "Second platform should be in the cart.");
    }
}
