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
import com.mcgill.ecse321.GameShop.model.SpecificGame;

import jakarta.transaction.Transactional;

@SpringBootTest
public class OrderRepositoryTests {

        private static List<String> testEmails = new ArrayList<String>();
        private static List<String> testTrackingNumbers = new ArrayList<String>();

        @Autowired
        private OrderRepository orderRepository;
        @Autowired
        private CustomerRepository customerRepository;
        @Autowired
        private CartRepository cartRepository;
        @Autowired
        private GameRepository gameRepository;
        @Autowired
        private SpecificGameRepository specificgameRepository;

        // Clear the database before and after each test
        @BeforeEach
        @AfterEach
        public void clearDatabase() {
                specificgameRepository.deleteAll();
                orderRepository.deleteAll();
                customerRepository.deleteAll();
                cartRepository.deleteAll();
                gameRepository.deleteAll();
                Account.clearTestEmails(OrderRepositoryTests.testEmails);
                OrderRepositoryTests.testEmails.clear();
                Account.clearTestEmails(OrderRepositoryTests.testTrackingNumbers);
                OrderRepositoryTests.testTrackingNumbers.clear();
        }

        @Test
        @Transactional
        public void TestCreateAndReadOrder() {
                // Define customer details
                String email = "14@14.com";
                String username = "JudeSousou";
                String password = "p123";
                String phoneNumber = "+1 (438) 320-239";
                String address = "1110 rue Sainte-Catherine";

                // Creating and saving Cart
                Cart cart = new Cart();
                cart = cartRepository.save(cart);

                // Define and save a Game
                String aDescription = "Halo2";
                int aPrice = 50;
                GameStatus aGameStatus = GameStatus.InStock;
                int aStockQuantity = 10;
                String aPhotoUrl = "www.halo2.com";
                Game firstGame = new Game(aDescription + "A", aDescription, aPrice, aGameStatus, aStockQuantity,
                                aPhotoUrl);
                firstGame = gameRepository.save(firstGame);

                // Create and save SpecificGame
                SpecificGame firstSpecificGame = new SpecificGame(firstGame);
                firstSpecificGame = specificgameRepository.save(firstSpecificGame);

                // Creating and saving Customer
                Customer firstCustomer = new Customer(email, username, password, phoneNumber, address, cart);
                firstCustomer = customerRepository.save(firstCustomer);
                OrderRepositoryTests.testEmails.add(firstCustomer.getEmail());

                // Define and save first Order
                Date orderDate = Date.valueOf("2021-10-10");
                String note = "testing";
                String paymentCard = "1234567890123456";
                Order firstOrder = new Order(orderDate, note, paymentCard, firstCustomer);
                firstOrder = orderRepository.save(firstOrder);
                OrderRepositoryTests.testTrackingNumbers.add(firstOrder.getTrackingNumber());

                // Associate SpecificGame with the first Order
                firstSpecificGame.addOrder(firstOrder);
                firstSpecificGame = specificgameRepository.save(firstSpecificGame);
                SpecificGame pulledFirstSpecificGame = specificgameRepository
                                .findById(firstSpecificGame.getSpecificGame_id());

                // Add Game to Cart
                cart.addGame(firstGame);

                // Get tracking number from the first Order
                String firstTrackingNumber = firstOrder.getTrackingNumber();

                // Define and save second Order
                Date orderDate2 = Date.valueOf("2021-10-12");
                String note2 = "testingorder2";
                String paymentCard2 = "1234567890123456";
                Order secondOrder = new Order(orderDate2, note2, paymentCard2, firstCustomer);
                secondOrder = orderRepository.save(secondOrder);
                OrderRepositoryTests.testTrackingNumbers.add(secondOrder.getTrackingNumber());

                // Get tracking number from the second Order
                String secondTrackingNumber = secondOrder.getTrackingNumber();

                // Retrieve and assert first Order details
                Order firstPulledOrder = orderRepository.findByTrackingNumber(firstTrackingNumber);
                assertNotNull(firstPulledOrder);
                assertEquals(orderDate, firstPulledOrder.getOrderDate());
                assertEquals(firstOrder.getTrackingNumber(), firstPulledOrder.getTrackingNumber());
                assertEquals(note, firstPulledOrder.getNote());
                assertEquals(paymentCard, firstPulledOrder.getPaymentCard());
                assertEquals(email, firstPulledOrder.getCustomer().getEmail());
                assertEquals(username, firstPulledOrder.getCustomer().getUsername());
                assertEquals(password, firstPulledOrder.getCustomer().getPassword());
                assertEquals(phoneNumber, firstPulledOrder.getCustomer().getPhoneNumber());
                assertEquals(address, firstPulledOrder.getCustomer().getAddress());
                assertEquals(firstGame.getGame_id(),
                                firstPulledOrder.getCustomer().getCart().getGames().getFirst().getGame_id());
                assertEquals(cart.getCart_id(),
                                firstPulledOrder.getCustomer().getCart().getCart_id());

                // Check if the first Order is associated with the SpecificGame
                String firstOrderTrackingNumber = firstOrder.getTrackingNumber();
                boolean foundOrder = pulledFirstSpecificGame.getOrder().stream()
                                .anyMatch(order -> order.getTrackingNumber().equals(firstOrderTrackingNumber));
                assertTrue(foundOrder, "First order should be in the specific game.");

                // Retrieve and assert second Order details
                Order secondPulledOrder = orderRepository.findByTrackingNumber(secondTrackingNumber);
                assertNotNull(secondPulledOrder);
                assertEquals(orderDate2, secondPulledOrder.getOrderDate());
                assertEquals(secondOrder.getTrackingNumber(), secondPulledOrder.getTrackingNumber());
                assertEquals(note2, secondPulledOrder.getNote());
                assertEquals(paymentCard2, secondPulledOrder.getPaymentCard());
                assertEquals(email, secondPulledOrder.getCustomer().getEmail());
                assertEquals(username, secondPulledOrder.getCustomer().getUsername());
                assertEquals(password, secondPulledOrder.getCustomer().getPassword());
                assertEquals(phoneNumber, secondPulledOrder.getCustomer().getPhoneNumber());
                assertEquals(address, secondPulledOrder.getCustomer().getAddress());
                assertEquals(cart.getCart_id(),
                                secondPulledOrder.getCustomer().getCart().getCart_id());

                // Assert that there are two Orders in the repository
                List<Order> orders = (List<Order>) orderRepository.findAll();
                assertEquals(2, orders.size());
        }
}
