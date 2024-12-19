package com.mcgill.ecse321.GameShop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.Cart;

import com.mcgill.ecse321.GameShop.model.Order;
import com.mcgill.ecse321.GameShop.repository.CustomerRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.OrderRepository;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CartService cartService;

    @Mock
    private SpecificGameService specificGameService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private OrderService orderService;

    private static final String VALID_TRACKING_NUMBER = "TRACK123";
    private static final String INVALID_TRACKING_NUMBER = "INVALID_TRACKNUMMBERRRRRR";
    private static final String VALID_CUSTOMER_EMAIL = "customer@tata.ca";
    private static final String INVALID_CUSTOMER_EMAIL = "invalid@ca.ca";
    private static final String VALID_NOTE = "Please deliver between 5-6 PM";
    private static final String VALID_PAYMENT_CARD = "1234567890123456";
    private static final LocalDate CURRENT_DATE = LocalDate.now();

    // --- Tests for getOrderByTrackingNumber ---

    @Test
    public void testGetOrderByTrackingNumberValid() {
        Cart cart = new Cart();
        cart.setCart_id(701);
        // Create a new customer
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL,
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);

        // Create a new order
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD,
                customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "1");

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "1")).thenReturn(
                order);

        // Act
        Order retrievedOrder = orderService.getOrderByTrackingNumber(VALID_TRACKING_NUMBER + "1");

        // Assert
        assertNotNull(retrievedOrder);
        assertEquals(VALID_TRACKING_NUMBER + "1", retrievedOrder.getTrackingNumber());

        verify(orderRepository,
                times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "1");
    }

    @Test
    public void testGetOrderByTrackingNumberInvalid() {
        when(orderRepository.findByTrackingNumber(INVALID_TRACKING_NUMBER)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.getOrderByTrackingNumber(INVALID_TRACKING_NUMBER);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository,
                times(1)).findByTrackingNumber(INVALID_TRACKING_NUMBER);
    }

    @Test
    public void testGetOrderByTrackingNumberNullTrackingNumber() {
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.getOrderByTrackingNumber(null);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, never()).findByTrackingNumber(anyString());
    }

    // --- Tests for createOrder ---

    @Test
    public void testCreateOrderValid() {
        // Arrange
        // Create a game
        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(8703);

        // Create a cart and add the game to it
        Cart cart = new Cart();
        cart.setCart_id(8702);
        cart.addGame(game);

        // Create a customer with the cart
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "ta",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);

        // Set up quantities
        Map<Integer, Integer> quantities = new HashMap<>();
        quantities.put(game.getGame_id(), 1); // Quantity of 2 for the game

        // Create SpecificGame objects
        SpecificGame specificGame = new SpecificGame(game);
        specificGame.setSpecificGame_id(8703);
        specificGame.setGames(game);
        specificGame.setItemStatus(SpecificGame.ItemStatus.Confirmed);

        List<SpecificGame> specificGameList = Arrays.asList(specificGame);

        // Mocking the repositories and services
        when(customerRepository.findByEmail(VALID_CUSTOMER_EMAIL + "ta")).thenReturn(customer);
        when(cartService.getQuantitiesForCart(cart.getCart_id())).thenReturn(quantities);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setTrackingNumber(VALID_TRACKING_NUMBER);
            return savedOrder;
        });
        when(specificGameService.getSpecificGamesByGameId(game.getGame_id())).thenReturn(specificGameList);

        // Set the order date
        LocalDate localDate = LocalDate.of(2023, 12, 12);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);

        // Act
        Order createdOrder = orderService.createOrder(
                VALID_ORDER_DATE,
                VALID_NOTE,
                VALID_PAYMENT_CARD,
                VALID_CUSTOMER_EMAIL + "ta");

        // Assert
        assertNotNull(createdOrder);
        assertEquals(VALID_TRACKING_NUMBER, createdOrder.getTrackingNumber());
        assertEquals(VALID_ORDER_DATE, createdOrder.getOrderDate());
        assertEquals(VALID_NOTE, createdOrder.getNote());
        assertEquals(VALID_PAYMENT_CARD, createdOrder.getPaymentCard());
        assertEquals(customer, createdOrder.getCustomer());

        // Verify that the cart is cleared
        verify(cartService, times(1)).clearCart(cart.getCart_id());

        // Verify interactions
        verify(customerRepository, times(1)).findByEmail(VALID_CUSTOMER_EMAIL + "ta");
        verify(cartService, times(1)).getQuantitiesForCart(cart.getCart_id());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(specificGameService, times(1)).getSpecificGamesByGameId(game.getGame_id());
        verify(gameService, times(1)).updateGameStockQuantity(eq(game.getGame_id()), anyInt());
    }

    @Test
    public void testCreateOrderWithInvalidCustomerEmail() {
        // Arrange
        when(customerRepository.findByEmail(INVALID_CUSTOMER_EMAIL + "lb")).thenReturn(null);
        LocalDate localDate = LocalDate.of(2023, 11, 12);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.createOrder(
                    VALID_ORDER_DATE,
                    VALID_NOTE,
                    VALID_PAYMENT_CARD,
                    INVALID_CUSTOMER_EMAIL + "lb");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Customer not found", exception.getMessage());

        verify(customerRepository, times(1)).findByEmail(INVALID_CUSTOMER_EMAIL + "lb");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrderWithNullCustomerEmail() {
        // Act & Assert
        LocalDate localDate = LocalDate.of(2023, 10, 12);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.createOrder(
                    VALID_ORDER_DATE,
                    VALID_NOTE,
                    VALID_PAYMENT_CARD,
                    null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Customer email cannot be empty or null", exception.getMessage());

        verify(customerRepository, never()).findByEmail(anyString());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrderWithEmptyCart() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(8710);

        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "re",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);

        when(customerRepository.findByEmail(VALID_CUSTOMER_EMAIL + "re")).thenReturn(customer);

        LocalDate localDate = LocalDate.of(2024, 10, 12);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.createOrder(
                    VALID_ORDER_DATE,
                    VALID_NOTE,
                    VALID_PAYMENT_CARD,
                    VALID_CUSTOMER_EMAIL + "re");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cart is empty", exception.getMessage());

        verify(customerRepository, times(1)).findByEmail(VALID_CUSTOMER_EMAIL + "re");
        verify(cartService, never()).getQuantitiesForCart(cart.getCart_id());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrderWithInsufficientStock() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(8711);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 1, "photoUrl");
        game.setGame_id(8705);
        cart.addGame(game);

        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "ek",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);

        Map<Integer, Integer> quantities = new HashMap<>();
        quantities.put(game.getGame_id(), 2); // Requesting 2 units, only 1 in stock
        LocalDate localDate = LocalDate.of(2024, 10, 9);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);

        when(customerRepository.findByEmail(VALID_CUSTOMER_EMAIL + "ek")).thenReturn(customer);
        when(cartService.getQuantitiesForCart(cart.getCart_id())).thenReturn(quantities);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.createOrder(
                    VALID_ORDER_DATE,
                    VALID_NOTE,
                    VALID_PAYMENT_CARD,
                    VALID_CUSTOMER_EMAIL + "ek");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(String.format("Insufficient stock for game ID %d", game.getGame_id()), exception.getMessage());

        verify(customerRepository, times(1)).findByEmail(VALID_CUSTOMER_EMAIL + "ek");
        verify(cartService, times(1)).getQuantitiesForCart(cart.getCart_id());
        verify(specificGameService, never()).getSpecificGamesByGameId(game.getGame_id());
        verify(orderRepository, never()).save(any(Order.class));
    }

    // --- Test updating an existing order ---
    @Test
    public void testUpdateOrderValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(8712);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "te",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);
        LocalDate localDate = LocalDate.of(2024, 10, 9);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);
        Order existingOrder = new Order(
                VALID_ORDER_DATE,
                VALID_NOTE,
                VALID_PAYMENT_CARD,
                customer);
        existingOrder.setTrackingNumber(VALID_TRACKING_NUMBER + "te");

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "te")).thenReturn(existingOrder);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Date newOrderDate = java.sql.Date.valueOf(localDate);
        String newNote = "Updated note";
        String newPaymentCard = "1234567890123457";

        // Act
        Order updatedOrder = orderService.updateOrder(
                VALID_TRACKING_NUMBER + "te",
                newOrderDate,
                newNote,
                newPaymentCard);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(VALID_TRACKING_NUMBER + "te", updatedOrder.getTrackingNumber());
        assertEquals(newOrderDate, updatedOrder.getOrderDate());
        assertEquals(newNote, updatedOrder.getNote());
        assertEquals(newPaymentCard, updatedOrder.getPaymentCard());

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "te");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrderNonExistent() {

        LocalDate localDate = LocalDate.of(2024, 10, 9);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);

        when(orderRepository.findByTrackingNumber(INVALID_TRACKING_NUMBER + "at")).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.updateOrder(
                    INVALID_TRACKING_NUMBER + "at",
                    VALID_ORDER_DATE,
                    VALID_NOTE,
                    VALID_PAYMENT_CARD);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findByTrackingNumber(INVALID_TRACKING_NUMBER + "at");
        verify(orderRepository, never()).save(any(Order.class));
    }

    // --- Test retrieving all orders ---
    @Test
    public void testGetAllOrders() {
        LocalDate localDate = LocalDate.of(2024, 10, 9);
        Date VALID_ORDER_DATE = java.sql.Date.valueOf(localDate);

        LocalDate newDate = LocalDate.of(2024, 10, 15);
        Date NEW_VALID_ORDER_DATE = java.sql.Date.valueOf(newDate);

        // Create a customer and two orders
        Cart cart = new Cart();
        cart.setCart_id(8717);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "tk",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);

        Order order1 = new Order(VALID_ORDER_DATE, "Note 1", "1234567890123456", customer);
        Order order2 = new Order(NEW_VALID_ORDER_DATE, "Note 2", "1234567890123456", customer);
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(orderRepository, times(1)).findAll();
    }

    // --- Tests for addGameToOrderOrder ---
    @Test
    public void testAddGameToOrderValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(57174);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "yes",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "yes");

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(571741);

        SpecificGame specificGame1 = new SpecificGame(game);
        SpecificGame specificGame2 = new SpecificGame(game);

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "yes")).thenReturn(order);
        when(gameRepository.findById(571741)).thenReturn(game);
        when(specificGameService.getSpecificGamesByGameId(571741))
                .thenReturn(Arrays.asList(specificGame1, specificGame2));

        // Act
        orderService.addGameToOrder(VALID_TRACKING_NUMBER + "yes", 571741, 2);

        // Assert
        assertEquals(8, game.getStockQuantity(), "Stock quantity should decrease by 2");
        assertTrue(specificGame1.getOrder().contains(order), "SpecificGame1 should be associated with the order");
        assertTrue(specificGame2.getOrder().contains(order), "SpecificGame2 should be associated with the order");

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "yes");
        verify(gameRepository, times(1)).findById(571741);
        verify(gameService, times(1)).updateGameStockQuantity(571741, game.getStockQuantity());
    }

    @Test
    public void testAddGameToOrderGameNotFound() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(571736);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "yees",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "yees");

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "yees")).thenReturn(order);
        when(gameRepository.findById(497174)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.addGameToOrder(VALID_TRACKING_NUMBER + "yees", 497174, 2);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game not found", exception.getMessage());

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "yees");
        verify(gameRepository, times(1)).findById(497174);
    }

    @Test
    public void testAddGameToOrderNotFound() {
        // Arrange
        when(orderRepository.findByTrackingNumber(INVALID_TRACKING_NUMBER + "tak")).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.addGameToOrder(INVALID_TRACKING_NUMBER + "tak", 497178, 1);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findByTrackingNumber(INVALID_TRACKING_NUMBER + "tak");
        verify(gameRepository, never()).findById(anyInt());
    }

    @Test
    public void testAddGameToOrderInsufficientStock() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(5717224);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "bey",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "bey");

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 1, "photoUrl");
        game.setGame_id(4971784);

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "bey")).thenReturn(order);
        when(gameRepository.findById(4971784)).thenReturn(game);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.addGameToOrder(VALID_TRACKING_NUMBER + "bey", 4971784, 2);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Insufficient stock", exception.getMessage());

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "bey");
        verify(gameRepository, times(1)).findById(4971784);
    }

    @Test
    public void testAddGameToOrderNotEnoughSpecificGamesAvailable() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(47172327);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "yul",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "yul");
        ;

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(4717232);

        SpecificGame specificGame1 = new SpecificGame(game);

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "yul")).thenReturn(order);
        when(gameRepository.findById(4717232)).thenReturn(game);
        when(specificGameService.getSpecificGamesByGameId(4717232))
                .thenReturn(Collections.singletonList(specificGame1)); // Only one SpecificGame available

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.addGameToOrder(VALID_TRACKING_NUMBER + "yul", 4717232, 2);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Not enough SpecificGame instances available for game ID " + 4717232,
                exception.getMessage());

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "yul");
        verify(gameRepository, times(1)).findById(4717232);
        verify(specificGameService, times(1)).getSpecificGamesByGameId(4717232);
    }

    // --- Tests for getSpecificGamesByOrder ---
    @Test
    public void testGetSpecificGamesByOrderSuccessful() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(8724);

        // Mock a Game and set the ID
        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(87257); // Set a game ID for reference

        // Add the game to the cart to ensure it's non-empty
        cart.addGame(game);

        // Create a Customer and Order, and associate them with the cart and game
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "akk1",
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);

        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "akk");

        // Create two SpecificGame instances for the game and mock their retrieval
        SpecificGame specificGame1 = new SpecificGame(game);
        specificGame1.setSpecificGame_id(20189);
        SpecificGame specificGame2 = new SpecificGame(game);
        specificGame2.setSpecificGame_id(202187);

        // Link SpecificGame instances to the game via specificGameService
        when(specificGameService.getSpecificGamesByGameId(87257))
                .thenReturn(Arrays.asList(specificGame1, specificGame2));
        when(specificGameService.getAllSpecificGames())
                .thenReturn(Arrays.asList(specificGame1, specificGame2));

        // Mock the gameRepository and orderRepository responses
        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "akk")).thenReturn(order);
        when(gameRepository.findById(87257)).thenReturn(game);

        // Act: Add game to order, then retrieve specific games by order
        orderService.addGameToOrder(VALID_TRACKING_NUMBER + "akk", 87257, 2);
        List<SpecificGame> result = orderService.getSpecificGamesByOrder(VALID_TRACKING_NUMBER + "akk");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(specificGame1), "Order should contain specificGame1");
        assertTrue(result.contains(specificGame2), "Order should contain specificGame2");

        // Verify interactions
        verify(orderRepository, times(2)).findByTrackingNumber(VALID_TRACKING_NUMBER + "akk");
        verify(gameRepository, times(1)).findById(87257);
        verify(specificGameService, times(1)).getSpecificGamesByGameId(87257);
    }

    @Test
    public void testGetSpecificGamesByOrderNotFound() {
        // Arrange
        when(orderRepository.findByTrackingNumber(INVALID_TRACKING_NUMBER + "oui")).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.getSpecificGamesByOrder(INVALID_TRACKING_NUMBER + "oui");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findByTrackingNumber(INVALID_TRACKING_NUMBER + "oui");
    }

    @Test
    public void testGetSpecificGamesByOrderEmpty() {
        // Arrange
        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "oui", "username", "password", "1234567890",
                "123 Street", new Cart());
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "empty");

        // Ensure no SpecificGame instances are associated with the order
        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "empty")).thenReturn(order);
        when(specificGameService.getAllSpecificGames()).thenReturn(Collections.emptyList());

        // Act
        List<SpecificGame> result = orderService.getSpecificGamesByOrder(VALID_TRACKING_NUMBER + "empty");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Result should be empty for an order with no specific games");

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "empty");
        verify(specificGameService, times(1)).getAllSpecificGames();
    }

    @Test
    public void testGetSpecificGamesByOrderMultipleSpecificGamesInOrder() {
        // Arrange
        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "alu", "username", "password", "1234567890",
                "123 Street",
                new Cart());
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "multiple");

        Game game1 = new Game("Title1", "Description1", 50, GameStatus.InStock, 5, "photoUrl1");
        game1.setGame_id(872578);
        Game game2 = new Game("Title2", "Description2", 60, GameStatus.InStock, 10, "photoUrl2");
        game2.setGame_id(872579);

        SpecificGame specificGame1 = new SpecificGame(game1);
        specificGame1.setSpecificGame_id(87247);
        specificGame1.addOrder(order);

        SpecificGame specificGame2 = new SpecificGame(game2);
        specificGame2.setSpecificGame_id(87248);
        specificGame2.addOrder(order);

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "multiple")).thenReturn(order);
        when(specificGameService.getAllSpecificGames()).thenReturn(Arrays.asList(specificGame1, specificGame2));

        // Act
        List<SpecificGame> result = orderService.getSpecificGamesByOrder(VALID_TRACKING_NUMBER + "multiple");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size(), "Expected two specific games in the order");
        assertTrue(result.contains(specificGame1), "Order should contain specificGame1");
        assertTrue(result.contains(specificGame2), "Order should contain specificGame2");

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "multiple");
        verify(specificGameService, times(1)).getAllSpecificGames();
    }

    @Test
    public void testGetSpecificGamesByOrderSpecificGameNotInOrder() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(872484);
        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "el", "username", "password", "1234567890",
                "123 Street", cart);
        Order order = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "no");

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(872488);

        SpecificGame unrelatedSpecificGame = new SpecificGame(game);
        unrelatedSpecificGame.setSpecificGame_id(872487); // This specific game is not added to the order

        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "no")).thenReturn(order);
        when(specificGameService.getAllSpecificGames()).thenReturn(Collections.singletonList(unrelatedSpecificGame));

        // Act
        List<SpecificGame> result = orderService.getSpecificGamesByOrder(VALID_TRACKING_NUMBER + "no");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Result should be empty as no specific games are associated with the order");

        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "no");
        verify(specificGameService, times(1)).getAllSpecificGames();
    }

    @Test
    public void testReturnGameSuccess() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(372484);

        // Create a game with initial stock quantity of 10
        Game game = new Game("GameTitle", "Description", 60, Game.GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(87248434);
        cart.addGame(game);

        // Create two SpecificGame instances
        SpecificGame specificGame1 = new SpecificGame(game);
        specificGame1.setSpecificGame_id(27248421);
        specificGame1.setItemStatus(SpecificGame.ItemStatus.Confirmed);

        SpecificGame specificGame2 = new SpecificGame(game);
        specificGame2.setSpecificGame_id(27248431);
        specificGame2.setItemStatus(SpecificGame.ItemStatus.Confirmed);

        // Create a customer and an order
        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "cana", "username", "password", "1234567890",
                "123 Street", cart);
        Order order = new Order(new Date(), "Return order note", "1234567890123456", customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "leb1");

        // Prepare a list of SpecificGames for the mocks
        List<SpecificGame> specificGamesList = new ArrayList<>();
        specificGamesList.add(specificGame1);
        specificGamesList.add(specificGame2);

        // Mocking behavior
        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "leb1")).thenReturn(order);
        when(specificGameService.findSpecificGameById(27248421)).thenReturn(specificGame1);
        when(gameRepository.findById(87248434)).thenReturn(game);
        when(specificGameService.getSpecificGamesByGameId(87248434)).thenReturn(specificGamesList);
        when(specificGameService.getAllSpecificGames()).thenReturn(specificGamesList); // For getSpecificGamesByOrder

        // Mock the updateGameStockQuantity to update the in-memory 'game' instance
        doAnswer(invocation -> {
            int newStockQuantity = invocation.getArgument(1);
            game.setStockQuantity(newStockQuantity);
            return null;
        }).when(gameService).updateGameStockQuantity(eq(game.getGame_id()), anyInt());

        // Act: Add two games to the order
        orderService.addGameToOrder(VALID_TRACKING_NUMBER + "leb1", 87248434, 2);

        // Verify that SpecificGames are now associated with the order
        assertTrue(specificGame1.getOrder().contains(order),
                "specificGame1 should be associated with the order after adding");
        assertTrue(specificGame2.getOrder().contains(order),
                "specificGame2 should be associated with the order after adding");

        // Act: Return one of the specific games
        orderService.returnGame(VALID_TRACKING_NUMBER + "leb1", 27248421, "damagedCD");

        // Assert
        // Check the item statuses
        assertEquals(SpecificGame.ItemStatus.Returned, specificGame1.getItemStatus(),
                "SpecificGame1 status should be set to Returned");
        assertEquals(SpecificGame.ItemStatus.Confirmed, specificGame2.getItemStatus(),
                "SpecificGame2 status should remain Confirmed");

        // Expected stock quantity:
        // Initial stock: 10
        // After adding 2 games to order: 10 - 2 = 8
        // After returning 1 game: 8 + 1 = 9
        assertEquals(9, game.getStockQuantity(),
                "Stock quantity should be 9 after returning one of two games");

        verify(gameService).updateGameStockQuantity(game.getGame_id(), 8); // After adding to order
        verify(gameService).updateGameStockQuantity(game.getGame_id(), 9); // After returning a game
        verify(gameService, times(2)).updateGameStockQuantity(eq(game.getGame_id()), anyInt());
        verify(specificGameService, times(1)).findSpecificGameById(27248421);
    }

    @Test
    public void testReturnGameAlreadyReturned() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(2724847);
        Game game = new Game("GameTitle", "Description", 60, Game.GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(2724848);
        cart.addGame(game);

        SpecificGame specificGame = new SpecificGame(game);
        specificGame.setSpecificGame_id(6724843);
        specificGame.setItemStatus(SpecificGame.ItemStatus.Returned); // Already returned

        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "co", "username", "password", "1234567890",
                "123 Street", cart);
        Order order = new Order(new Date(), "Order note", "1234567890123456", customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "co");

        // Associate the specific game with the order
        specificGame.addOrder(order);

        // Mocking behavior
        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "co")).thenReturn(order);
        when(specificGameService.findSpecificGameById(6724843)).thenReturn(specificGame);
        when(specificGameService.getAllSpecificGames()).thenReturn(Collections.singletonList(specificGame));

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.returnGame(VALID_TRACKING_NUMBER + "co", 6724843, "");
        });

        // Verify exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game already returned", exception.getMessage());

        // Verify interactions
        verify(orderRepository, times(2)).findByTrackingNumber(VALID_TRACKING_NUMBER + "co");
        verify(specificGameService, times(1)).findSpecificGameById(6724843);
        verify(specificGameService, never()).updateSpecificGameItemStatus(anyInt(), any());
        verify(gameService, never()).updateGameStockQuantity(anyInt(), anyInt());
    }

    @Test
    public void testReturnGameGameNotInOrder() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(67248434);
        Game game = new Game("AnotherGame", "Description", 50, Game.GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(67248435);
        cart.addGame(game);

        SpecificGame specificGame = new SpecificGame(game);
        specificGame.setSpecificGame_id(67248436);
        specificGame.setItemStatus(SpecificGame.ItemStatus.Confirmed);

        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "wa", "user", "pass", "0987654321", "456 Avenue",
                cart);
        Order order = new Order(new Date(), "Order note", "1234567890123456", customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "24");

        // Mocking behavior
        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "24")).thenReturn(order);
        when(specificGameService.findSpecificGameById(67248436)).thenReturn(specificGame);
        when(specificGameService.getAllSpecificGames()).thenReturn(Collections.emptyList());

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.returnGame(VALID_TRACKING_NUMBER + "24", 67248436, null);
        });

        // Verify exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game not in order", exception.getMessage());

        // Verify interactions
        verify(orderRepository, times(2)).findByTrackingNumber(VALID_TRACKING_NUMBER + "24");
        verify(specificGameService, times(1)).findSpecificGameById(67248436);
        verify(specificGameService, never()).updateSpecificGameItemStatus(anyInt(), any());
        verify(gameService, never()).updateGameStockQuantity(anyInt(), anyInt());
    }

    @Test
    public void testReturnGameSpecificGameNotFound() {

        Cart cart = new Cart();
        cart.setCart_id(67248411);
        Game game = new Game("AnotherGame", "Description", 50, Game.GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(67248410);
        cart.addGame(game);

        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "wb", "user", "pass", "0987654321", "456 Avenue", cart);
        Order order = new Order(new Date(), "Order note", "1234567890123456", customer);
        order.setTrackingNumber(INVALID_TRACKING_NUMBER + "25");

        // Arrange
        when(orderRepository.findByTrackingNumber(INVALID_TRACKING_NUMBER + "25")).thenReturn(order);
        when(specificGameService.findSpecificGameById(67248412)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.returnGame(INVALID_TRACKING_NUMBER + "25", 67248412, "");
        });

        // Verify exception details
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("SpecificGame not found", exception.getMessage());

        // Verify interactions
        verify(specificGameService, times(1)).findSpecificGameById(67248412);
        verify(orderRepository, times(1)).findByTrackingNumber(anyString());
        verify(specificGameService, never()).updateSpecificGameItemStatus(anyInt(), any());
        verify(gameService, never()).updateGameStockQuantity(anyInt(), anyInt());
    }

    @Test
    public void testReturnGameOrderNotFound() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(67248465);
        Game game = new Game("AnotherGame", "Description", 50, Game.GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(67248464);
        cart.addGame(game);

        SpecificGame specificGame = new SpecificGame(game);
        specificGame.setSpecificGame_id(672484653);
        specificGame.setItemStatus(SpecificGame.ItemStatus.Confirmed);

        when(orderRepository.findByTrackingNumber(INVALID_TRACKING_NUMBER + "23")).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.returnGame(INVALID_TRACKING_NUMBER + "23", 67248463, "");
        });

        // Verify exception details
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Order not found", exception.getMessage());

        // Verify interactions
        verify(orderRepository, times(1)).findByTrackingNumber(INVALID_TRACKING_NUMBER + "23");
        verify(specificGameService, never()).findSpecificGameById(67248463);
        verify(specificGameService, never()).updateSpecificGameItemStatus(anyInt(), any());
        verify(gameService, never()).updateGameStockQuantity(anyInt(), anyInt());
    }

    @Test
    public void testReturnGameAfter7Days() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(4728472);

        Game game = new Game("Old Game", "Past Description", 60, Game.GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(4728473);
        cart.addGame(game);

        SpecificGame specificGame = new SpecificGame(game);
        specificGame.setSpecificGame_id(4728474);
        specificGame.setItemStatus(SpecificGame.ItemStatus.Confirmed);

        Customer customer = new Customer(VALID_CUSTOMER_EMAIL + "late", "lateUser", "latePass", "0987654321",
                "Late Ave",
                cart);

        // Set order date to 8 days before the CURRENT_DATE
        Date pastDate = java.sql.Date.valueOf(CURRENT_DATE.minusDays(8));
        Order order = new Order(pastDate, "Late order note", "1234567890123456", customer);
        order.setTrackingNumber(VALID_TRACKING_NUMBER + "late");

        // Mock
        when(orderRepository.findByTrackingNumber(VALID_TRACKING_NUMBER + "late")).thenReturn(order);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.returnGame(VALID_TRACKING_NUMBER + "late", 4728474, "");
        });

        // Verify exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game return period has expired. Returns are allowed within 7 days of the order date.",
                exception.getMessage());

        // Verify interactions
        verify(orderRepository, times(1)).findByTrackingNumber(VALID_TRACKING_NUMBER + "late");
        verify(specificGameService, never()).findSpecificGameById(4728474);
        verify(specificGameService, never()).updateSpecificGameItemStatus(anyInt(), any());
        verify(gameService, never()).updateGameStockQuantity(anyInt(), anyInt());
    }

    // --- Tests for getOrdersWithCustomerEmail ---

    @Test
    public void testGetOrdersWithCustomerEmailValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(6472820);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "iphone",
                "testUser",
                "testPass",
                "1234567890",
                "456 Avenue",
                cart);

        Order order1 = new Order(new Date(), VALID_NOTE, VALID_PAYMENT_CARD, customer);
        Order order2 = new Order(new Date(), "Second order", VALID_PAYMENT_CARD, customer);

        List<Order> orders = Arrays.asList(order1, order2);

        // Mocking the repositories
        when(customerRepository.findByEmail(VALID_CUSTOMER_EMAIL + "iphone")).thenReturn(customer);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> retrievedOrders = orderService.getOrdersWithCustomerEmail(VALID_CUSTOMER_EMAIL + "iphone");

        // Assert
        assertNotNull(retrievedOrders);
        assertEquals(2, retrievedOrders.size());
        assertTrue(retrievedOrders.contains(order1), "Retrieved orders should contain order1");
        assertTrue(retrievedOrders.contains(order2), "Retrieved orders should contain order2");

        // Verify interactions
        verify(customerRepository, times(1)).findByEmail(VALID_CUSTOMER_EMAIL + "iphone");
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrdersWithCustomerEmailInvalidEmail() {
        // Arrange
        when(customerRepository.findByEmail(INVALID_CUSTOMER_EMAIL + "helloworld")).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.getOrdersWithCustomerEmail(INVALID_CUSTOMER_EMAIL + "helloworld");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Customer not found", exception.getMessage());

        // Verify interactions
        verify(customerRepository, times(1)).findByEmail(INVALID_CUSTOMER_EMAIL + "helloworld");
        verify(orderRepository, never()).findAll();
    }

    @Test
    public void testGetOrdersWithCustomerEmailNoOrdersFound() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(6472820);
        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL + "byebye",
                "testUser",
                "testPass",
                "1234567890",
                "456 Avenue",
                cart);

        when(customerRepository.findByEmail(VALID_CUSTOMER_EMAIL + "byebye")).thenReturn(customer);
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.getOrdersWithCustomerEmail(VALID_CUSTOMER_EMAIL + "byebye");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No orders found for customer email: " + VALID_CUSTOMER_EMAIL + "byebye", exception.getMessage());

        // Verify interactions
        verify(customerRepository, times(1)).findByEmail(VALID_CUSTOMER_EMAIL + "byebye");
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrdersWithCustomerEmailNullEmail() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.getOrdersWithCustomerEmail(null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Customer email cannot be empty or null", exception.getMessage());

        // Verify interactions
        verify(customerRepository, never()).findByEmail(anyString());
        verify(orderRepository, never()).findAll();
    }

    @Test
    public void testGetOrdersWithCustomerEmailEmptyEmail() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            orderService.getOrdersWithCustomerEmail("");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Customer email cannot be empty or null", exception.getMessage());

        // Verify interactions
        verify(customerRepository, never()).findByEmail(anyString());
        verify(orderRepository, never()).findAll();
    }

}