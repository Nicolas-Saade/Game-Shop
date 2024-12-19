package com.mcgill.ecse321.GameShop.repository;

import java.sql.Date;
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
import com.mcgill.ecse321.GameShop.model.Order;

import jakarta.transaction.Transactional;

@SpringBootTest
public class CartRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();
    private static List<String> testTrackingNumbers = new ArrayList<String>();

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear all repositories to ensure a clean state before and after each test
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        cartRepository.deleteAll();
        gameRepository.deleteAll();
        Account.clearTestEmails(CartRepositoryTests.testEmails);
        CartRepositoryTests.testEmails.clear();
        Order.clearTestTrackingNumbers(CartRepositoryTests.testTrackingNumbers);
        CartRepositoryTests.testTrackingNumbers.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadCartFull() {
        // Create and save first game
        String aDescription = "Halo2";
        int aPrice = 50;
        GameStatus aGameStatus = GameStatus.InStock;
        int aStockQuantity = 10;
        String aPhotoUrl = "www.halo2.com";
        Game firstGame = new Game(aDescription + "A", aDescription, aPrice, aGameStatus, aStockQuantity, aPhotoUrl);
        firstGame = gameRepository.save(firstGame);

        // Create and save second game
        String aDescription2 = "CsGo";
        int aPrice2 = 15;
        GameStatus aGameStatus2 = GameStatus.OutOfStock;
        int aStockQuantity2 = 0;
        String aPhotoUrl2 = "www.csgo.com";
        Game secondGame = new Game(aDescription2 + "A", aDescription2, aPrice2, aGameStatus2, aStockQuantity2,
                aPhotoUrl2);
        secondGame = gameRepository.save(secondGame);

        // Create and save cart with games
        Date aOrderDate = Date.valueOf("2022-04-05");
        Cart cart = new Cart();
        cart.addGame(firstGame);
        cart.addGame(secondGame);
        cart = cartRepository.save(cart);

        // Create and save customer with cart
        Customer aCustomer = new Customer("4@4.com", "ajn", "ajn2", "1234567890", "123 street", cart);
        aCustomer = customerRepository.save(aCustomer);
        CartRepositoryTests.testEmails.add(aCustomer.getEmail());

        // Create and save order with customer
        Order order = new Order(aOrderDate, "notes of the order", "1234567890123456", aCustomer);
        order = orderRepository.save(order);
        CartRepositoryTests.testTrackingNumbers.add(order.getTrackingNumber());

        // Associate order with cart and save
        cart = cartRepository.save(cart);

        // Retrieve and verify cart
        int cartId = cart.getCart_id();
        Cart pulledCart = cartRepository.findById(cartId);
        assertNotNull(pulledCart, "Cart should not be null.");
        assertEquals(cart.getCart_id(), pulledCart.getCart_id(), "Cart ID should match.");
        assertEquals(2, pulledCart.getGames().size(), "Cart should have 2 games.");

        // Verify games in cart
        int firstGameId = firstGame.getGame_id();
        int secondGameId = secondGame.getGame_id();
        boolean foundFirstGame = pulledCart.getGames().stream().anyMatch(game -> game.getGame_id() == firstGameId);
        assertTrue(foundFirstGame, "First game should be in the cart.");
        boolean foundSecondGame = pulledCart.getGames().stream().anyMatch(game -> game.getGame_id() == secondGameId);
        assertTrue(foundSecondGame, "Second game should be in the cart.");
        assertEquals(GameStatus.InStock, pulledCart.getGames().get(0).getGameStatus(),
                "First game's status should be InStock.");
        assertEquals(GameStatus.OutOfStock, pulledCart.getGames().get(1).getGameStatus(),
                "Second game's status should be OutOfStock.");
        assertEquals("Halo2", pulledCart.getGames().get(0).getDescription(), "First game should be Halo2.");
        assertEquals("CsGo", pulledCart.getGames().get(1).getDescription(), "Second game should be CsGo.");
        assertEquals(50, pulledCart.getGames().get(0).getPrice(), "First game's price should be 50.");
        assertEquals(15, pulledCart.getGames().get(1).getPrice(), "Second game's price should be 15.");
    }

    @Test
    @Transactional
    public void testCreateAndReadCartEmpty() {
        // Create and save an empty cart
        Cart cart = new Cart();
        cart = cartRepository.save(cart);

        // Retrieve and verify the empty cart
        int cartId = cart.getCart_id();
        Cart pulledCart = cartRepository.findById(cartId);
        assertEquals(cart.getCart_id(), pulledCart.getCart_id(), "Cart ID should match.");
        assertNotNull(pulledCart, "Cart should not be null");
        assertEquals(0, pulledCart.getGames().size(), "This cart should have no games");
    }
}