package com.mcgill.ecse321.GameShop.repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.model.SpecificGame.ItemStatus;

import jakarta.transaction.Transactional;

@SpringBootTest
public class SpecificGameRepositoryTests {

        private static List<String> testEmails = new ArrayList<String>();
        private static List<String> testTrackingNumbers = new ArrayList<String>();

        @Autowired
        private GameRepository gameRepository;
        @Autowired
        private SpecificGameRepository specificGameRepository;
        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private CartRepository cartRepository;
        @Autowired
        private CustomerRepository customerRepository;

        @BeforeEach
        @AfterEach
        public void clearDatabase() {
                // Clear all repositories to ensure a clean state before and after each test
                specificGameRepository.deleteAll();
                orderRepository.deleteAll();
                customerRepository.deleteAll();
                cartRepository.deleteAll();
                gameRepository.deleteAll();
                Account.clearTestEmails(SpecificGameRepositoryTests.testEmails);
                SpecificGameRepositoryTests.testEmails.clear();
                Account.clearTestEmails(SpecificGameRepositoryTests.testTrackingNumbers);
                SpecificGameRepositoryTests.testTrackingNumbers.clear();
        }

        @Test
        @Transactional
        public void TestCreateAndReadSpecificGame() {
                // Create and save a Game entity
                String title = "game1";
                String description = "game1_description";
                int price = 25;
                GameStatus gameStatus = GameStatus.OutOfStock;
                int stockQuantity = 50;
                String photoUrl = "wwww.photo1.com";
                ItemStatus itemStatus = ItemStatus.Confirmed;

                Game game = new Game(title, description, price, gameStatus, stockQuantity, photoUrl);
                game = gameRepository.save(game);

                // Create and save a SpecificGame entity associated with the Game
                SpecificGame specificGame = new SpecificGame(game);
                specificGame.setItemStatus(itemStatus);
                specificGame = specificGameRepository.save(specificGame);

                // Create and save a Customer entity
                String email = "judes10@gmail.com";
                String username = "JudeSousou1";
                String password = "p1234";
                String phoneNumber = "+1 (438) 320-239";
                String address = "1110 rue Sainte-Catherine";

                Cart cart = new Cart();
                cart = cartRepository.save(cart);

                Customer customer1 = new Customer(email, username, password, phoneNumber, address, cart);
                customer1 = customerRepository.save(customer1);
                SpecificGameRepositoryTests.testEmails.add(customer1.getEmail());

                // Create and save an Order entity associated with the Customer
                Date orderDate = Date.valueOf("2021-10-10");
                String note = "testing";
                String paymentCard = "1234567890123456";

                Order order = new Order(orderDate, note, paymentCard, customer1);
                order = orderRepository.save(order);
                SpecificGameRepositoryTests.testTrackingNumbers.add(order.getTrackingNumber());

                // Associate the SpecificGame with the Order
                String trackingNumber = order.getTrackingNumber();
                Order orderFromDb = orderRepository.findByTrackingNumber(trackingNumber);
                specificGame.addOrder(orderFromDb);
                specificGame = specificGameRepository.save(specificGame);

                // Retrieve and verify the SpecificGame from the database
                SpecificGame specificGameFromDb = specificGameRepository.findById(specificGame.getSpecificGame_id());

                assertNotNull(specificGameFromDb);
                assertEquals(itemStatus, specificGameFromDb.getItemStatus());
                assertEquals(title, specificGameFromDb.getGames().getTitle());
                assertEquals(description, specificGameFromDb.getGames().getDescription());
                assertEquals(price, specificGameFromDb.getGames().getPrice());
                assertEquals(gameStatus, specificGameFromDb.getGames().getGameStatus());
                assertEquals(stockQuantity, specificGameFromDb.getGames().getStockQuantity());
                assertEquals(photoUrl, specificGameFromDb.getGames().getPhotoUrl());
                assertEquals(note, specificGameFromDb.getOrder().getLast().getNote());
                assertEquals(orderDate, specificGameFromDb.getOrder().getLast().getOrderDate());
                assertEquals(paymentCard, specificGameFromDb.getOrder().getLast().getPaymentCard());
                assertEquals(email, specificGameFromDb.getOrder().getLast().getCustomer().getEmail());
                assertEquals(username, specificGameFromDb.getOrder().getLast().getCustomer().getUsername());
                assertEquals(password, specificGameFromDb.getOrder().getLast().getCustomer().getPassword());
                assertEquals(phoneNumber, specificGameFromDb.getOrder().getLast().getCustomer().getPhoneNumber());
                assertEquals(address, specificGameFromDb.getOrder().getLast().getCustomer().getAddress());
        }

        @Test
        @Transactional
        public void testMultipleSpecificGamesWithSameGame() {
                // Verify that multiple SpecificGames can be associated with the same Game.

                // Create and save a Game entity
                Game game = new Game("SharedGame", "Game shared among SpecificGames", 90, GameStatus.InStock, 100,
                                "http://sharedgame.url");
                game = gameRepository.save(game);

                // Create and save SpecificGame 1 associated with the Game
                SpecificGame specificGame1 = new SpecificGame(game);
                specificGame1.setItemStatus(ItemStatus.Confirmed);
                specificGame1 = specificGameRepository.save(specificGame1);

                // Create and save SpecificGame 2 associated with the same Game
                SpecificGame specificGame2 = new SpecificGame(game);
                specificGame2.setItemStatus(ItemStatus.Returned);
                specificGame2 = specificGameRepository.save(specificGame2);

                // Retrieve and verify SpecificGames from the database
                SpecificGame pulledSpecificGame1 = specificGameRepository.findById(specificGame1.getSpecificGame_id());
                SpecificGame pulledSpecificGame2 = specificGameRepository.findById(specificGame2.getSpecificGame_id());

                assertNotNull(pulledSpecificGame1);
                assertNotNull(pulledSpecificGame2);
                assertEquals(game.getGame_id(), pulledSpecificGame1.getGames().getGame_id(),
                                "SpecificGame1 should be associated with the Game.");
                assertEquals(game.getGame_id(), pulledSpecificGame2.getGames().getGame_id(),
                                "SpecificGame2 should be associated with the Game.");
                assertNotEquals(pulledSpecificGame1.getSpecificGame_id(), pulledSpecificGame2.getSpecificGame_id());
        }

        @Test
        @Transactional
        public void testMultipleSpecificGamesWithSameOrder() {
                // Verify that multiple SpecificGames can be associated with the same Order.

                // Create and save Game entities
                Game game1 = new Game("OrderGame1", "Game1 for Order", 100, GameStatus.InStock, 50,
                                "http://ordergame1.url");
                Game game2 = new Game("OrderGame2", "Game2 for Order", 110, GameStatus.InStock, 60,
                                "http://ordergame2.url");
                game1 = gameRepository.save(game1);
                game2 = gameRepository.save(game2);

                // Create and save SpecificGames associated with the Games
                SpecificGame specificGame1 = new SpecificGame(game1);
                specificGame1.setItemStatus(ItemStatus.Confirmed);
                specificGame1 = specificGameRepository.save(specificGame1);

                SpecificGame specificGame2 = new SpecificGame(game2);
                specificGame2.setItemStatus(ItemStatus.Confirmed);
                specificGame2 = specificGameRepository.save(specificGame2);

                // Create and save a Customer and Order entity
                Cart cart = cartRepository.save(new Cart());
                Customer customer = customerRepository.save(
                                new Customer("400@400.com", "400", "password", "+1 (400) 456-7890", "400 rue Ottawa",
                                                cart));
                Order order = orderRepository
                                .save(new Order(Date.valueOf("2021-03-01"), "OrderNote", "1234567890123456", customer));
                SpecificGameRepositoryTests.testEmails.add(customer.getEmail());
                SpecificGameRepositoryTests.testTrackingNumbers.add(order.getTrackingNumber());

                // Associate both SpecificGames with the same Order
                specificGame1.addOrder(order);
                specificGame2.addOrder(order);
                specificGame1 = specificGameRepository.save(specificGame1);
                specificGame2 = specificGameRepository.save(specificGame2);

                // Retrieve and verify Orders from the database
                SpecificGame pulledSpecificGame1 = specificGameRepository.findById(specificGame1.getSpecificGame_id());
                SpecificGame pulledSpecificGame2 = specificGameRepository.findById(specificGame2.getSpecificGame_id());

                assertNotNull(pulledSpecificGame1);
                assertNotNull(pulledSpecificGame2);
                assertEquals(1, pulledSpecificGame1.getOrder().size(), "SpecificGame1 should have 1 Order.");
                assertEquals(1, pulledSpecificGame2.getOrder().size(), "SpecificGame2 should have 1 Order.");
                assertEquals(order.getTrackingNumber(), pulledSpecificGame1.getOrder().get(0).getTrackingNumber(),
                                "Order should be associated with SpecificGame1.");
                assertEquals(order.getTrackingNumber(), pulledSpecificGame2.getOrder().get(0).getTrackingNumber(),
                                "Order should be associated with SpecificGame2.");
        }
}