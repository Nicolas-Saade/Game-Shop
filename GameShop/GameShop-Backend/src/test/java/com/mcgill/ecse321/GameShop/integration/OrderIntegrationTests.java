package com.mcgill.ecse321.GameShop.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderAddGameRequestDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderListDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderRequestDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderResponseDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountRequestDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountResponseDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartRequestDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameListDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameRequestDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameResponseDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CartRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.OrderRepository;
import com.mcgill.ecse321.GameShop.repository.SpecificGameRepository;
import com.mcgill.ecse321.GameShop.repository.WishListRepository;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.model.SpecificGame.ItemStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class OrderIntegrationTests {

        @Autowired
        private TestRestTemplate client;

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private GameRepository gameRepository;

        @Autowired
        private WishListRepository wishListRepository;

        @Autowired
        private SpecificGameRepository specificGameRepository;

        @Autowired
        private CartRepository cartRepository;

        private String trackingNumber;
        private String customerEmail = "customer@hellohello.com";
        private int gameId;
        private int specificGameId;
        private String paymentCard = "1234567890123456";

        @BeforeAll
        @AfterAll
        public void clearDatabase() {
                specificGameRepository.deleteAll();
                wishListRepository.deleteAll();
                orderRepository.deleteAll();
                accountRepository.deleteAll();
                cartRepository.deleteAll();
                gameRepository.deleteAll();
        }

        @Test
        @Order(1)
        public void testCreateValidOrder() {
                // Create Customer Account
                AccountRequestDto customerRequest = new AccountRequestDto(
                                customerEmail,
                                "customerUser",
                                "customerPass",
                                "123-456-7890",
                                "123 Customer Street");
                ResponseEntity<AccountResponseDto> customerResponse = client.postForEntity(
                                "/account/customer",
                                customerRequest,
                                AccountResponseDto.class);
                assertNotNull(customerResponse);
                assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
                AccountResponseDto customer = customerResponse.getBody();
                assertNotNull(customer);
                assertEquals(customerEmail, customer.getEmail());

                // Create a Game
                GameRequestDto gameRequest = new GameRequestDto(
                                "Test Game",
                                "Test Description",
                                50,
                                GameStatus.InStock,
                                10,
                                "http://example.com/game.jpg");
                ResponseEntity<GameResponseDto> gameResponse = client.postForEntity(
                                "/games",
                                gameRequest,
                                GameResponseDto.class);
                assertNotNull(gameResponse);
                assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
                GameResponseDto game = gameResponse.getBody();
                assertNotNull(game);
                gameId = game.getaGame_id();

                // Retrieve or Create Customer's Cart
                ResponseEntity<CartResponseDto> cartResponse = client.getForEntity(
                                "/carts/customer/" + customerEmail,
                                CartResponseDto.class);
                assertEquals(HttpStatus.OK, cartResponse.getStatusCode());
                CartResponseDto cart = cartResponse.getBody();
                assertNotNull(cart);

                int cartId = cart.getCartId();

                // Add Game to Customer's Cart
                CartRequestDto cartRequestDto = new CartRequestDto(gameId, 1);
                ResponseEntity<CartResponseDto> updatedCartResponse = client.exchange(
                                "/carts/" + cartId + "/games",
                                HttpMethod.PUT,
                                new HttpEntity<>(cartRequestDto),
                                CartResponseDto.class);
                assertNotNull(updatedCartResponse);
                assertEquals(HttpStatus.OK, updatedCartResponse.getStatusCode());

                // Create Specific Game Instance for Order
                SpecificGameRequestDto specificGameRequest = new SpecificGameRequestDto(
                                SpecificGame.ItemStatus.Confirmed,
                                new ArrayList<>(),
                                gameId);
                ResponseEntity<SpecificGameResponseDto> specificGameResponse = client.postForEntity(
                                "/specificGames",
                                specificGameRequest,
                                SpecificGameResponseDto.class);
                assertNotNull(specificGameResponse);
                assertEquals(HttpStatus.OK, specificGameResponse.getStatusCode());
                SpecificGameResponseDto specificGame = specificGameResponse.getBody();
                assertNotNull(specificGame);
                specificGameId = specificGame.getSpecificGame_id();

                List<Integer> specificGamesIds = new ArrayList<>();
                specificGamesIds.add(specificGameId);

                // Create Order with Customer’s Cart
                OrderRequestDto orderRequest = new OrderRequestDto(
                                new Date(),
                                "Please deliver between 9 AM - 5 PM",
                                paymentCard,
                                customerEmail,
                                specificGamesIds);

                ResponseEntity<OrderResponseDto> orderResponse = client.postForEntity(
                                "/orders",
                                orderRequest,
                                OrderResponseDto.class);

                assertNotNull(orderResponse);
                assertEquals(HttpStatus.OK, orderResponse.getStatusCode());
                OrderResponseDto order = orderResponse.getBody();
                assertNotNull(order);
                trackingNumber = order.getTrackingNumber();

                // Final Assertions
                assertNotNull(trackingNumber);
                assertEquals(customerEmail, order.getCustomerEmail());
        }

        @Test
        @Order(2)
        public void testGetOrdersByCustomerEmail() {

                // Call the endpoint to retrieve orders for the specified customer
                ResponseEntity<OrderListDto> response = client.getForEntity(
                                "/orders/customer/" + customerEmail,
                                OrderListDto.class);

                // Assert that response is non-null and status is OK
                assertNotNull(response, "Response was null; expected a valid response entity.");
                assertEquals(HttpStatus.OK, response.getStatusCode(),
                                "Expected status OK but got " + response.getStatusCode());

                // Extract order list from response and verify it contains data
                OrderListDto orders = response.getBody();
                assertNotNull(orders, "Expected non-null OrderListDto, but got null");

                // Ensure there's at least one order and that the order belongs to the correct
                // customer
                assertFalse(orders.getOrders().isEmpty(), "Expected orders, but found none");
                orders.getOrders().forEach(order -> {
                        assertEquals(customerEmail, order.getCustomerEmail(),
                                        "Order customer email does not match the expected email");
                });
        }

        @Test
        @Order(3)
        public void testGetOrderByTrackingNumber() {
                // Retrieve order by tracking number
                ResponseEntity<OrderResponseDto> response = client.getForEntity(
                                "/orders/" + trackingNumber,
                                OrderResponseDto.class);

                // Assertions
                assertNotNull(response, "Response was null; expected a valid response entity.");
                assertEquals(HttpStatus.OK, response.getStatusCode(),
                                "Expected status OK but got " + response.getStatusCode());

                OrderResponseDto order = response.getBody();
                assertNotNull(order, "Expected non-null OrderResponseDto, but got null");

                // Check if tracking number and customer email match
                assertEquals(trackingNumber, order.getTrackingNumber(), "Tracking number mismatch");
                assertEquals(customerEmail, order.getCustomerEmail(), "Customer email mismatch");

                // Ensure specific games are returned in the order
                assertFalse(order.getSpecificGames().isEmpty(), "Expected specific games in the order, but found none");
        }

        @Test
        @Order(4)
        public void testUpdateOrder() {
                // Updated details
                String updatedNote = "Updated delivery instructions";
                Date updatedOrderDate = new Date(); // Current date for simplicity
                String updatedPaymentCard = "9876543210123456"; // New payment card

                OrderRequestDto updateRequest = new OrderRequestDto(
                                updatedOrderDate,
                                updatedNote,
                                updatedPaymentCard,
                                customerEmail,
                                null // No specific games update
                );

                // Send update request
                ResponseEntity<OrderResponseDto> response = client.exchange(
                                "/orders/" + trackingNumber,
                                HttpMethod.PUT,
                                new HttpEntity<>(updateRequest),
                                OrderResponseDto.class);

                // Assertions
                assertNotNull(response, "Response was null; expected a valid response entity.");
                assertEquals(HttpStatus.OK, response.getStatusCode(),
                                "Expected status OK but got " + response.getStatusCode());

                OrderResponseDto updatedOrder = response.getBody();
                assertNotNull(updatedOrder, "Expected non-null OrderResponseDto, but got null");

                // Verify that the updated fields match the expected values
                assertEquals(updatedNote, updatedOrder.getNote(), "Order note mismatch");
        }

        @Test
        @Order(5)
        public void testAddGameToOrder() {
                // Create a new game
                GameRequestDto newGameRequest = new GameRequestDto(
                                "Additional Test Game",
                                "Description for additional game",
                                50,
                                GameStatus.InStock,
                                10,
                                "http://example.com/additional_game.jpg");

                ResponseEntity<GameResponseDto> gameResponse = client.postForEntity(
                                "/games",
                                newGameRequest,
                                GameResponseDto.class);
                assertNotNull(gameResponse);
                assertEquals(HttpStatus.OK, gameResponse.getStatusCode());

                GameResponseDto additionalGame = gameResponse.getBody();
                assertNotNull(additionalGame);
                int additionalGameId = additionalGame.getaGame_id();

                // **Create SpecificGame instances for the new game**
                SpecificGameRequestDto specificGameRequest = new SpecificGameRequestDto(
                                SpecificGame.ItemStatus.Confirmed,
                                new ArrayList<>(),
                                additionalGameId);

                // Let's create multiple SpecificGame instances if needed
                for (int i = 0; i < 5; i++) {
                        ResponseEntity<SpecificGameResponseDto> specificGameResponse = client.postForEntity(
                                        "/specificGames",
                                        specificGameRequest,
                                        SpecificGameResponseDto.class);
                        assertNotNull(specificGameResponse);
                        assertEquals(HttpStatus.OK, specificGameResponse.getStatusCode());
                }

                // Add the game to the existing order
                OrderAddGameRequestDto addGameRequest = new OrderAddGameRequestDto(additionalGameId, 1);
                ResponseEntity<OrderResponseDto> response = client.exchange(
                                "/orders/" + trackingNumber + "/games",
                                HttpMethod.PUT,
                                new HttpEntity<>(addGameRequest),
                                OrderResponseDto.class);

                assertNotNull(response);
                assertEquals(HttpStatus.OK, response.getStatusCode());

                OrderResponseDto order = response.getBody();
                assertNotNull(order);
                assertTrue(order.getSpecificGames().size() >= 2);
        }

        @Test
        @Order(6)
        public void testAddAnotherSpecificGameToOrder() {
                // Create a second SpecificGame instance for the same Game
                SpecificGameRequestDto specificGameRequest = new SpecificGameRequestDto(
                                SpecificGame.ItemStatus.Confirmed,
                                new ArrayList<>(),
                                gameId);

                ResponseEntity<SpecificGameResponseDto> specificGameResponse = client.postForEntity(
                                "/specificGames",
                                specificGameRequest,
                                SpecificGameResponseDto.class);
                assertNotNull(specificGameResponse);
                assertEquals(HttpStatus.OK, specificGameResponse.getStatusCode());

                SpecificGameResponseDto specificGame = specificGameResponse.getBody();
                assertNotNull(specificGame);
                int specificGameId2 = specificGame.getSpecificGame_id();

                // Add the second SpecificGame instance to the order
                OrderAddGameRequestDto addGameRequest = new OrderAddGameRequestDto(gameId, 1);
                ResponseEntity<OrderResponseDto> response = client.exchange(
                                "/orders/" + trackingNumber + "/games",
                                HttpMethod.PUT,
                                new HttpEntity<>(addGameRequest),
                                OrderResponseDto.class);

                assertNotNull(response);
                assertEquals(HttpStatus.OK, response.getStatusCode());

                ResponseEntity<OrderResponseDto> updatedOrderResponse = client.getForEntity(
                                "/orders/" + trackingNumber,
                                OrderResponseDto.class);

                assertNotNull(updatedOrderResponse);
                assertEquals(HttpStatus.OK, updatedOrderResponse.getStatusCode());

                OrderResponseDto updatedOrder = updatedOrderResponse.getBody();
                assertNotNull(updatedOrder);

                List<Integer> specificGameIds = updatedOrder.getSpecificGames().stream()
                                .map(SpecificGameResponseDto::getSpecificGame_id)
                                .collect(Collectors.toList());

                assertTrue(specificGameIds.contains(specificGameId));
                assertTrue(specificGameIds.contains(specificGameId2));
        }

        @Test
        @Order(7)
        public void testGetAllOrders() {
                // Send request to get all orders
                ResponseEntity<OrderListDto> response = client.getForEntity(
                                "/orders",
                                OrderListDto.class);

                // Assertions
                assertNotNull(response, "Response was null; expected a valid response entity.");
                assertEquals(HttpStatus.OK, response.getStatusCode(),
                                "Expected status OK but got " + response.getStatusCode());

                OrderListDto orderList = response.getBody();
                assertNotNull(orderList, "Expected non-null OrderListDto, but got null");

                // Check we have at least one order in the list
                assertFalse(orderList.getOrders().isEmpty(), "Expected at least one order, but found none");
                // Assert one of the orders has the expected tracking number
                assertTrue(orderList.getOrders().stream()
                                .anyMatch(order -> order.getTrackingNumber().equals(trackingNumber)),
                                "Expected to find the order with tracking number: " + trackingNumber);
        }

        @Test
        @Order(8)
        public void testGetSpecificGamesByOrder() {
                // Call the endpoint to get specific games for the existing order
                ResponseEntity<SpecificGameListDto> response = client.getForEntity(
                                "/orders/" + trackingNumber + "/specificGames",
                                SpecificGameListDto.class);

                // Assertions
                assertNotNull(response, "Response was null; expected a valid response entity.");
                assertEquals(HttpStatus.OK, response.getStatusCode(),
                                "Expected status OK but got " + response.getStatusCode());

                SpecificGameListDto specificGameListDto = response.getBody();
                assertNotNull(specificGameListDto, "Expected non-null SpecificGameListDto, but got null");

                // Extract the list of specific game summaries
                List<SpecificGameSummaryDto> specificGames = specificGameListDto.getGames();

                assertNotNull(specificGames, "Expected non-null list of SpecificGameSummaryDto, but got null");
                assertFalse(specificGames.isEmpty(), "Expected specific games, but found none");

                // Check available fields in each SpecificGameSummaryDto
                specificGames.forEach(sg -> {
                        assertNotNull(sg.getSpecificGame_id(), "Expected specific game ID to be present");
                        assertNotNull(sg.getItemStatus(), "Expected item status to be present");
                });
        }

        @Test
        @Order(9)
        public void testReturnGame() {
                // Retrieve initial stock quantity for accurate assertions
                GameResponseDto initialGame = client.getForObject("/games/" + gameId, GameResponseDto.class);
                assertNotNull(initialGame, "Expected non-null GameResponseDto, but got null");
                int initialStockQuantity = initialGame.getaStockQuantity();

                // Create a specific game instance to be added to an order
                SpecificGameRequestDto specificGameRequest = new SpecificGameRequestDto(
                                SpecificGame.ItemStatus.Confirmed,
                                new ArrayList<>(),
                                gameId);

                ResponseEntity<SpecificGameResponseDto> specificGameResponse = client.postForEntity(
                                "/specificGames",
                                specificGameRequest,
                                SpecificGameResponseDto.class);
                assertNotNull(specificGameResponse);
                assertEquals(HttpStatus.OK, specificGameResponse.getStatusCode());

                SpecificGameResponseDto specificGame = specificGameResponse.getBody();
                assertNotNull(specificGame);
                int specificGameId = specificGame.getSpecificGame_id();

                // Add the specific game to an existing order
                OrderAddGameRequestDto addGameRequest = new OrderAddGameRequestDto(gameId, 1);
                ResponseEntity<OrderResponseDto> addGameResponse = client.exchange(
                                "/orders/" + trackingNumber + "/games",
                                HttpMethod.PUT,
                                new HttpEntity<>(addGameRequest),
                                OrderResponseDto.class);
                assertNotNull(addGameResponse);
                assertEquals(HttpStatus.OK, addGameResponse.getStatusCode());

                // Return the specific game within the order
                ResponseEntity<OrderResponseDto> returnGameResponse = client.exchange(
                                "/orders/" + trackingNumber + "/return/" + specificGameId,
                                HttpMethod.PUT,
                                null,
                                OrderResponseDto.class);

                // Assertions to confirm return success
                assertNotNull(returnGameResponse, "Response was null; expected a valid response entity.");
                assertEquals(HttpStatus.OK, returnGameResponse.getStatusCode(),
                                "Expected status OK but got " + returnGameResponse.getStatusCode());

                OrderResponseDto updatedOrder = returnGameResponse.getBody();
                assertNotNull(updatedOrder, "Expected non-null OrderResponseDto, but got null");

                // Verify that the specific game's status is updated to "Returned"
                boolean isReturned = updatedOrder.getSpecificGames().stream()
                                .anyMatch(sg -> sg.getSpecificGame_id() == specificGameId
                                                && sg.getItemStatus().equals(ItemStatus.Returned));
                assertTrue(isReturned, "Expected specific game to be marked as Returned, but it was not.");

                // Verify stock quantity is incremented by 1 after returning
                GameResponseDto updatedGame = client.getForObject("/games/" + gameId, GameResponseDto.class);
                assertNotNull(updatedGame, "Expected non-null GameResponseDto, but got null");
                assertEquals(initialStockQuantity, updatedGame.getaStockQuantity(),
                                "Expected stock quantity to be restored to initial quantity after return.");

                // Retrieve the specific game again to confirm item status is "Returned"
                SpecificGameResponseDto returnedSpecificGame = client.getForObject("/specificGames/" + specificGameId,
                                SpecificGameResponseDto.class);
                assertNotNull(returnedSpecificGame, "Expected non-null SpecificGameResponseDto after return.");
                assertEquals(ItemStatus.Returned, returnedSpecificGame.getItemStatus(),
                                "Expected specific game status to be 'Returned' after returning.");
        }

        @SuppressWarnings("null")
        @Test
        @Order(10)
        public void testCreateOrderWithInvalidCustomerEmail() {
                OrderRequestDto orderRequest = new OrderRequestDto(
                                new Date(),
                                "Invalid order with non-existent customer",
                                paymentCard,
                                "invalid_email@nonexistent.com", // Invalid email
                                new ArrayList<>());

                ResponseEntity<String> response = client.postForEntity("/orders", orderRequest, String.class);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                                "Expected status NOT_FOUND for invalid customer email.");
                assertTrue(response.getBody().contains("Customer not found"),
                                "Expected 'Customer not found' error message.");
        }

        @SuppressWarnings("null")
        @Test
        @Order(11)
        public void testGetOrderByInvalidTrackingNumber() {
                ResponseEntity<String> response = client.getForEntity("/orders/invalid_tracking_number", String.class);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                                "Expected status NOT_FOUND for invalid tracking number.");
                assertTrue(response.getBody().contains("Order not found"), "Expected 'Order not found' error message.");
        }

        @SuppressWarnings("null")
        @Test
        @Order(12)
        public void testAddGameToOrderWithInvalidGameId() {
                OrderAddGameRequestDto addGameRequest = new OrderAddGameRequestDto(9999999, 1); // Invalid game ID

                ResponseEntity<String> response = client.exchange(
                                "/orders/" + trackingNumber + "/games",
                                HttpMethod.PUT,
                                new HttpEntity<>(addGameRequest),
                                String.class);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                                "Expected status NOT_FOUND for invalid game ID.");
                assertTrue(response.getBody().contains("Game not found"), "Expected 'Game not found' error message.");
        }

        @SuppressWarnings("null")
        @Test
        @Order(13)
        public void testReturnNonExistentSpecificGame() {
                int nonExistentSpecificGameId = 91929398;

                ResponseEntity<String> response = client.exchange(
                                "/orders/" + trackingNumber + "/return/" + nonExistentSpecificGameId,
                                HttpMethod.PUT,
                                null,
                                String.class);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertTrue(response.getBody().contains("SpecificGame does not exist"));
        }

        @SuppressWarnings("null")
        @Test
        @Order(14)
        public void testReturnGameNotInOrder() {
                // Create a new specific game but do not add it to the existing order
                SpecificGameRequestDto specificGameRequest = new SpecificGameRequestDto(
                                SpecificGame.ItemStatus.Confirmed,
                                new ArrayList<>(),
                                gameId);

                ResponseEntity<SpecificGameResponseDto> specificGameResponse = client.postForEntity(
                                "/specificGames",
                                specificGameRequest,
                                SpecificGameResponseDto.class);
                assertNotNull(specificGameResponse);

                SpecificGameResponseDto specificGame = specificGameResponse.getBody();
                assertNotNull(specificGame);
                int specificGameIdNotInOrder = specificGame.getSpecificGame_id();

                // Attempt to return a specific game that was never added to the order
                ResponseEntity<String> response = client.exchange(
                                "/orders/" + trackingNumber + "/return/" + specificGameIdNotInOrder,
                                HttpMethod.PUT,
                                null,
                                String.class);

                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertTrue(response.getBody().contains("Game not in order"));
        }

}
