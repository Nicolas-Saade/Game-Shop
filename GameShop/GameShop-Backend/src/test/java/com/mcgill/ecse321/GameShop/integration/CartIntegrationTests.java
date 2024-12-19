package com.mcgill.ecse321.GameShop.integration;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountRequestDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountResponseDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartListDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartRequestDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartResponseDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartSummaryDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CartRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.OrderRepository;
import com.mcgill.ecse321.GameShop.repository.ReplyRepository;
import com.mcgill.ecse321.GameShop.repository.WishListRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CartIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private WishListRepository wishlistRepo;
    @Autowired
    private ReplyRepository replyRepo;

    @Autowired
    private OrderRepository orderRepo;

    private int cartId;
    private int gameId1;
    private int gameId2;
    private String customerEmail = "customerofcart@example.com";
    private String customerUsername = "customeruser";
    private String customerPassword = "customerpass";
    private String customerPhone = "123-456-7890";
    private String customerAddress = "123 Customer Street";

    @BeforeAll
    @AfterAll
    public void clearDatabase() {
        wishlistRepo.deleteAll();

        replyRepo.deleteAll();

        orderRepo.deleteAll();
        accountRepo.deleteAll();
        cartRepo.deleteAll();
        gameRepo.deleteAll();

    }

    /**
     * Test creating a cart by creating a customer (since cart is associated with
     * customer)
     */
    @Test
    @Order(1)
    public void testCreateCartWithCustomerAndGetIt() {
        // Arrange: Create a Customer (which should automatically create a Cart)
        AccountRequestDto customerRequest = new AccountRequestDto(
                customerEmail,
                customerUsername,
                customerPassword,
                customerPhone,
                customerAddress);
        ResponseEntity<AccountResponseDto> customerResponse = client.postForEntity(
                "/account/customer",
                customerRequest,
                AccountResponseDto.class);

        // Assert: Verify that the customer was created successfully
        assertNotNull(customerResponse);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        AccountResponseDto customer = customerResponse.getBody();
        assertNotNull(customer);
        assertEquals(customerEmail, customer.getEmail());

        // Arrange: Get the cart associated with the customer
        String url = String.format("/carts/customer/%s", customerEmail);

        // Act: Send a GET request to retrieve the cart
        ResponseEntity<CartResponseDto> cartResponse = client.getForEntity(url, CartResponseDto.class);

        // Assert: Verify that the cart was retrieved successfully
        assertNotNull(cartResponse);
        assertEquals(HttpStatus.OK, cartResponse.getStatusCode());
        CartResponseDto cart = cartResponse.getBody();
        assertNotNull(cart);
        cartId = cart.getCartId();
        assertTrue(cartId > 0);
    }

    /**
     * Test getting the cart by customer email
     */
    @Test
    @Order(2)
    public void testGetCartByCustomerEmail() {
        // Arrange
        String url = String.format("/carts/customer/%s", customerEmail);

        // Act
        ResponseEntity<CartResponseDto> response = client.getForEntity(url, CartResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartResponseDto cart = response.getBody();
        assertNotNull(cart);
        assertEquals(cartId, cart.getCartId());
        assertEquals(0, cart.getTotalItems());
        assertEquals(0, cart.getTotalPrice());
        assertTrue(cart.getGames().isEmpty());
    }

    /**
     * Test getting the cart with non-existent customer email
     */
    @Test
    @Order(3)
    public void testGetCartByNonExistentCustomerEmail() {
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";
        String url = String.format("/carts/customer/%s", nonExistentEmail);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        System.out.println(body);
        assertTrue(body.contains("Customer not found"));
    }

    /**
     * Test adding a game to the cart
     */
    @Test
    @Order(4)
    public void testAddGameToCart() {
        GameRequestDto gameRequest1 = new GameRequestDto(
                "Game One",
                "First game description",
                50,
                GameStatus.InStock,
                10,
                "http://example.com/game1.jpg");
        ResponseEntity<GameResponseDto> gameResponse1 = client.postForEntity("/games", gameRequest1,
                GameResponseDto.class);
        assertNotNull(gameResponse1);
        assertEquals(HttpStatus.OK, gameResponse1.getStatusCode());
        GameResponseDto game1 = gameResponse1.getBody();
        assertNotNull(game1);
        gameId1 = game1.getaGame_id();

        // Act: Add game to cart
        String url = String.format("/carts/%d/games", cartId);
        CartRequestDto requestDto = new CartRequestDto(gameId1, 2);
        ResponseEntity<CartResponseDto> response = client.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                CartResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartResponseDto cart = response.getBody();
        assertNotNull(cart);
        assertEquals(cartId, cart.getCartId());
        assertEquals(2, cart.getTotalItems());
        assertEquals(100, cart.getTotalPrice());
        assertEquals(1, cart.getGames().size());
        assertEquals(gameId1, cart.getGames().get(0).getGame().getaGame_id());
        assertEquals(2, cart.getGames().get(0).getQuantity());
    }

    /**
     * Test adding a game to the cart with invalid quantity
     */
    @Test
    @Order(5)
    public void testAddGameToCartInvalidQuantity() {
        // Arrange
        String url = String.format("/carts/%d/games", cartId);
        CartRequestDto requestDto = new CartRequestDto(gameId1, -1);

        // Act
        ResponseEntity<String> response = client.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                String.class);

        // Assert
        assertNotNull(response, "Response was null; expected a valid response entity.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Expected status BAD_REQUEST for invalid quantity.");
        String body = response.getBody();
        assertNotNull(body, "Response body was null; expected error message.");
        assertTrue(body.contains("Quantity must be at least 1"), "Expected error message about invalid quantity.");
    }

    /**
     * Test removing a game from the cart
     */
    @Test
    @Order(6)
    public void testRemoveGameFromCart() {
        // Arrange: Add another game to cart
        GameRequestDto gameRequest2 = new GameRequestDto(
                "Game Two",
                "Second game description",
                70,
                GameStatus.InStock,
                5,
                "http://example.com/game2.jpg");
        ResponseEntity<GameResponseDto> gameResponse2 = client.postForEntity("/games", gameRequest2,
                GameResponseDto.class);
        assertNotNull(gameResponse2);
        assertEquals(HttpStatus.OK, gameResponse2.getStatusCode());
        GameResponseDto game2 = gameResponse2.getBody();
        assertNotNull(game2);
        gameId2 = game2.getaGame_id();

        String addUrl = String.format("/carts/%d/games", cartId);
        CartRequestDto addRequestDto = new CartRequestDto(gameId2, 3);
        ResponseEntity<CartResponseDto> addResponse = client.exchange(
                addUrl,
                HttpMethod.PUT,
                new HttpEntity<>(addRequestDto),
                CartResponseDto.class);
        assertNotNull(addResponse);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());

        // Act: Remove some quantity of gameId2 from cart
        String removeUrl = String.format("/carts/%d/games/remove", cartId);
        CartRequestDto removeRequestDto = new CartRequestDto(gameId2, 2);
        ResponseEntity<CartResponseDto> response = client.exchange(
                removeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(removeRequestDto),
                CartResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartResponseDto cart = response.getBody();
        assertNotNull(cart);
        assertEquals(3, cart.getTotalItems()); // 2 of gameId1, 1 of gameId2
        assertTrue(cart.getGames().stream().anyMatch(g -> g.getGame().getaGame_id() == gameId1));
        assertTrue(cart.getGames().stream().anyMatch(g -> g.getGame().getaGame_id() == gameId2));
        assertEquals(50 * 2 + 70 * 1, cart.getTotalPrice());

    }

    /**
     * Test removing a game from the cart with invalid quantity
     */
    @Test
    @Order(7)
    public void testRemoveGameFromCartInvalidQuantity() {
        // Arrange
        String url = String.format("/carts/%d/games/remove", cartId);
        CartRequestDto requestDto = new CartRequestDto(gameId2, -1);

        // Act
        ResponseEntity<String> response = client.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                String.class);

        // Assert
        assertNotNull(response, "Response was null; expected a valid response entity.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Expected status BAD_REQUEST for invalid quantity.");
        String body = response.getBody();
        assertNotNull(body, "Response body was null; expected error message.");
        assertTrue(body.contains("Quantity must be at least 1"), "Expected error message about invalid quantity.");
    }

    /**
     * Test updating the quantity of a game in the cart
     */
    @Test
    @Order(8)
    public void testUpdateGameQuantityInCart() {
        // Arrange: Ensure initial quantity of gameId1 is set to 1
        String addUrl = String.format("/carts/%d/games", cartId);
        CartRequestDto addRequestDto = new CartRequestDto(gameId1, 1);
        ResponseEntity<CartResponseDto> addResponse = client.exchange(
                addUrl,
                HttpMethod.PUT,
                new HttpEntity<>(addRequestDto),
                CartResponseDto.class);
        assertNotNull(addResponse);
        CartResponseDto cartesponse = addResponse.getBody();
        assertNotNull(cartesponse);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode(), cartesponse.toString());

        // Arrange: Update quantity of gameId1 to 5
        String url = String.format("/carts/%d/games/%d/quantity", cartId, gameId1);
        CartRequestDto requestDto = new CartRequestDto(gameId1, 5);

        // Act
        ResponseEntity<CartResponseDto> response = client.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                CartResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "the second one");
        CartResponseDto cart = response.getBody();
        assertNotNull(cart);
        assertEquals(6, cart.getTotalItems()); // 5 of gameId1, 1 of gameId2
        assertEquals(50 * 5 + 70 * 1, cart.getTotalPrice());
    }

    /**
     * Test updating the quantity of a game in the cart with invalid quantity
     * //
     */
    @Test
    @Order(9)
    public void testUpdateGameQuantityInCartInvalidQuantity() {
        // Arrange
        String url = String.format("/carts/%d/games/%d/quantity", cartId, gameId1);
        CartRequestDto requestDto = new CartRequestDto(gameId1, -2);

        // Act
        ResponseEntity<String> response = client.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        System.out.println(body);
        assertTrue(body.contains("Quantity must be at least 1"));
    }

    // /**
    // * Test clearing the cart
    // */
    @Test
    @Order(10)
    public void testClearCart() {
        // Arrange
        String url = String.format("/carts/%d/clear", cartId);

        // Act
        ResponseEntity<CartResponseDto> response = client.exchange(
                url,
                HttpMethod.PUT,
                null,
                CartResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartResponseDto cart = response.getBody();
        assertNotNull(cart);
        assertEquals(0, cart.getTotalItems());
        assertEquals(0, cart.getTotalPrice());
        assertTrue(cart.getGames().isEmpty());
    }

    /**
     * Test getting all games from the cart (should be empty after clearing)
     */
    @Test
    @Order(11)
    public void testGetGamesInCartEmpty() {
        // Arrange
        String url = String.format("/carts/%d/games", cartId);

        // Act
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameListDto games = response.getBody();
        assertNotNull(games);
        assertTrue(games.getGames().isEmpty());
    }

    /**
     * Test getting a specific game from the cart
     */
    @Test
    @Order(12)
    public void testGetGameFromCart() {
        // Arrange: Add game back to cart
        String addUrl = String.format("/carts/%d/games", cartId);
        CartRequestDto addRequestDto = new CartRequestDto(gameId1, 2);
        ResponseEntity<CartResponseDto> addResponse = client.exchange(
                addUrl,
                HttpMethod.PUT,
                new HttpEntity<>(addRequestDto),
                CartResponseDto.class);

        // Assert: Verify that the game was added successfully
        assertNotNull(addResponse, "Response was null; expected a valid response entity.");
        assertEquals(HttpStatus.OK, addResponse.getStatusCode(), "Expected status OK for adding game to cart.");

        // Act: Retrieve the game from the cart
        String url = String.format("/carts/%d/games/%d", cartId, gameId1);
        ResponseEntity<GameResponseDto> response = client.getForEntity(url, GameResponseDto.class);

        // Assert: Verify the retrieved game details
        assertNotNull(response, "Response was null; expected a valid response entity.");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected status OK for retrieving game from cart.");
        GameResponseDto game = response.getBody();
        assertNotNull(game, "GameResponseDto was null; expected a valid game response.");
        assertEquals(gameId1, game.getaGame_id(), "Game ID mismatch; expected the added game's ID.");
    }

    /**
     * Test getting a non-existent game from the cart
     */
    @Test
    @Order(13)
    public void testGetNonExistentGameFromCart() {
        // Arrange
        int nonExistentGameId = 9999;
        String url = String.format("/carts/%d/games/%d", cartId, nonExistentGameId);

        // Act
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Game with ID " + nonExistentGameId + " does not exist in the cart"));
    }

    /**
     * Test getting all carts
     */
    @Test
    @Order(14)
    public void testGetAllCarts() {
        // Arrange: Create another customer with cart
        AccountRequestDto customerRequest = new AccountRequestDto(
                "anothercustomer@example.com",
                "anotherUser",
                "anotherPass",
                "987-654-3210",
                "456 Another Street");
        ResponseEntity<AccountResponseDto> customerResponse = client.postForEntity(
                "/account/customer",
                customerRequest,
                AccountResponseDto.class);
        assertNotNull(customerResponse);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());

        // Act
        ResponseEntity<CartListDto> response = client.getForEntity("/carts", CartListDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartListDto carts = response.getBody();
        assertNotNull(carts);
        List<CartSummaryDto> cartSummaries = carts.getCarts();
        assertNotNull(cartSummaries);
        assertTrue(cartSummaries.size() >= 2);
    }

    /**
     * Test adding a game to cart with insufficient stock
     */
    @Test
    @Order(15)
    public void testAddGameToCartInsufficientStock() {
        // Arrange: Create a new game with limited stock
        GameRequestDto gameRequest = new GameRequestDto(
                "Low Stock Game",
                "Game with limited stock for test",
                50, // price
                GameStatus.InStock,
                1, // Set low stock to 1
                "http://example.com/game_low_stock.jpg");
        ResponseEntity<GameResponseDto> gameResponse = client.postForEntity("/games", gameRequest,
                GameResponseDto.class);
        assertNotNull(gameResponse, "Response was null; expected a valid response entity.");
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode(), "Expected status OK for game creation.");

        GameResponseDto game = gameResponse.getBody();
        assertNotNull(game, "GameResponseDto was null; expected a valid response.");
        int createdGameId = game.getaGame_id(); // Use this ID for the current test

        // Act: Try to add more than available stock (quantity of 5) to the cart
        String url = String.format("/carts/%d/games", cartId);
        CartRequestDto requestDto = new CartRequestDto(createdGameId, 5);
        ResponseEntity<String> response = client.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                String.class);

        // Assert: Expect BAD_REQUEST due to insufficient stock
        assertNotNull(response, "Response was null; expected a valid response entity.");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
                "Expected status BAD_REQUEST due to insufficient stock.");
        String body = response.getBody();
        assertNotNull(body, "Response body was null; expected an error message.");
        assertTrue(body.contains("Only 1 units of Game ID " + createdGameId + " are available"),
                "Expected error message indicating insufficient stock.");
    }

    /**
     * Test adding a non-existent game to cart
     */
    @Test
    @Order(16)
    public void testAddNonExistentGameToCart() {
        // Arrange
        int nonExistentGameId = 9999;
        String url = String.format("/carts/%d/games", cartId);
        CartRequestDto requestDto = new CartRequestDto(nonExistentGameId, 1);

        // Act
        ResponseEntity<String> response = client.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(requestDto),
                String.class);

        // Assert
        assertNotNull(response, "Response was null; expected a valid response entity.");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Expected status NOT_FOUND for non-existent game.");
        String body = response.getBody();
        assertNotNull(body, "Response body was null; expected an error message.");
        assertTrue(body.contains("Game with ID " + nonExistentGameId + " does not exist"),
                "Expected error message indicating game does not exist.");
    }

    @Test
    @Order(17)
    public void testFindCartById_Valid() {
        // Arrange: Create a new customer and retrieve the automatically created cart ID
        AccountRequestDto customerRequest = new AccountRequestDto(
                "validcartcustomer@example.com",
                "validUser",
                "validPassword",
                "123-456-7890",
                "123 Test Address");
        ResponseEntity<AccountResponseDto> customerResponse = client.postForEntity(
                "/account/customer",
                customerRequest,
                AccountResponseDto.class);
        assertNotNull(customerResponse);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());

        // Extract the cartId
        String url = String.format("/carts/customer/%s", "validcartcustomer@example.com");
        ResponseEntity<CartResponseDto> cartResponse = client.getForEntity(url, CartResponseDto.class);
        assertNotNull(cartResponse);
        assertEquals(HttpStatus.OK, cartResponse.getStatusCode());
        CartResponseDto cart = cartResponse.getBody();
        assertNotNull(cart);
        int cartId = cart.getCartId();

        // Act: Find the cart by ID
        String findUrl = String.format("/carts/%d", cartId);
        ResponseEntity<CartResponseDto> response = client.getForEntity(findUrl, CartResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartResponseDto foundCart = response.getBody();
        assertNotNull(foundCart);
        assertEquals(cartId, foundCart.getCartId());
        assertEquals(0, foundCart.getTotalItems()); // Initially empty cart
        assertEquals(0, foundCart.getTotalPrice()); // No items, so price is 0
    }

    @Test
    @Order(18)
    public void testFindCartById_Invalid() {
        // Arrange: Define a non-existent cart ID
        int nonExistentCartId = 99999;

        // Act: Try to retrieve the cart by a non-existent ID
        String url = String.format("/carts/%d", nonExistentCartId);
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        // Assert: Expect 404 NOT_FOUND status
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("Cart with ID " + nonExistentCartId + " does not exist"));
    }

}