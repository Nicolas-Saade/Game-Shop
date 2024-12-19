package com.mcgill.ecse321.GameShop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CartRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;

import java.util.*;

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
public class CartServiceTests {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CartService cartService;

    private static final int VALID_CART_ID = 1;
    private static final int INVALID_CART_ID = 999;
    private static final int VALID_GAME_ID = 100;
    private static final Integer VALID_QUANTITY = 2;
    private static final String VALID_CUSTOMER_EMAIL = "customer@gmail.ca";
    private static final String INVALID_CUSTOMER_EMAIL = "invalid@ca.ca";

    // --- Tests for getCartByCustomerEmail ---

    @Test
    public void testGetCartByCustomerEmailValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(VALID_CART_ID);

        Customer customer = new Customer(
                VALID_CUSTOMER_EMAIL,
                "username",
                "password",
                "1234567890",
                "123 Street",
                cart);

        when(accountRepository.findByEmail(VALID_CUSTOMER_EMAIL)).thenReturn(customer);

        // Act
        Cart retrievedCart = cartService.getCartByCustomerEmail(VALID_CUSTOMER_EMAIL);

        // Assert
        assertNotNull(retrievedCart);
        assertEquals(VALID_CART_ID, retrievedCart.getCart_id());

        verify(accountRepository, times(1)).findByEmail(VALID_CUSTOMER_EMAIL);
    }

    @Test
    public void testGetCartByCustomerEmailInvalidEmail() {
        // Arrange
        when(accountRepository.findByEmail(INVALID_CUSTOMER_EMAIL)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.getCartByCustomerEmail(INVALID_CUSTOMER_EMAIL);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Customer not found", exception.getMessage());

        verify(accountRepository, times(1)).findByEmail(INVALID_CUSTOMER_EMAIL);
    }

    // --- Tests for getCartById ---

    @Test
    public void testGetCartByIdValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(11);

        when(cartRepository.findById(11)).thenReturn(cart);

        // Act
        Cart retrievedCart = cartService.getCartById(11);

        // Assert
        assertNotNull(retrievedCart);
        assertEquals(11, retrievedCart.getCart_id());

        verify(cartRepository, times(1)).findById(11);
    }

    @Test
    public void testGetCartByIdInvalid() {
        // Arrange
        when(cartRepository.findById(INVALID_CART_ID)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.getCartById(INVALID_CART_ID);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Cart with ID %d does not exist", INVALID_CART_ID), exception.getMessage());

        verify(cartRepository, times(1)).findById(INVALID_CART_ID);
    }

    // --- Tests for addGameToCart ---

    @Test
    public void testAddGameToCartValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(12);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(VALID_GAME_ID);

        when(cartRepository.findById(12)).thenReturn(cart);
        when(gameRepository.findById(VALID_GAME_ID)).thenReturn(game);
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        Cart updatedCart = cartService.addGameToCart(12, VALID_GAME_ID, VALID_QUANTITY);

        // Assert
        assertNotNull(updatedCart);
        assertTrue(updatedCart.getGames().contains(game));

        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(12);
        assertEquals(VALID_QUANTITY, quantities.get(VALID_GAME_ID));

        verify(cartRepository, times(1)).findById(12); // getCartById is called twice
        verify(gameRepository, times(1)).findById(VALID_GAME_ID);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void testAddGameToCartInvalidGameId() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(1206);

        when(cartRepository.findById(1206)).thenReturn(cart);
        when(gameRepository.findById(1207)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(1206, 1207, VALID_QUANTITY);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Game with ID %d does not exist", 1207), exception.getMessage());

        verify(cartRepository, times(1)).findById(1206);
        verify(gameRepository, times(1)).findById(1207);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddGameToCartGameOutOfStock() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(1367);

        Game game = new Game("Title", "Description", 50, GameStatus.OutOfStock, 0, "photoUrl");
        game.setGame_id(13671);

        when(cartRepository.findById(1367)).thenReturn(cart);
        when(gameRepository.findById(13671)).thenReturn(game);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(1367, 13671, VALID_QUANTITY);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(String.format("Game with ID %d is not available for purchase", 13671), exception.getMessage());

        verify(cartRepository, times(1)).findById(1367);
        verify(gameRepository, times(1)).findById(13671);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddGameToCartQuantityExceedsStock() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(1571);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(1572);

        when(cartRepository.findById(1571)).thenReturn(cart);
        when(gameRepository.findById(1572)).thenReturn(game);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(1571, 1572, 10);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(
                String.format("Only %d units of Game ID %d are available", game.getStockQuantity(), 1572),
                exception.getMessage());

        verify(cartRepository, times(1)).findById(1571);
        verify(gameRepository, times(1)).findById(1572);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testAddGameToCartInvalidCartId() {
        // Arrange
        when(cartRepository.findById(112)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(112, 113, VALID_QUANTITY);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Cart with ID %d does not exist", 112), exception.getMessage());

        verify(cartRepository, times(1)).findById(112);
        verify(gameRepository, never()).findById(anyInt());
    }

    @Test
    public void testAddGameToCartNullQuantity() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(11564);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(114);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(11564, 114, null);
        });

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Quantity must be at least 1.", exception.getMessage());

        verify(cartRepository, never()).findById(anyInt());
        verify(gameRepository, never()).findById(anyInt());
    }

    @Test
    public void testAddGameToCartZeroOrNegativeQuantity() {
        // Arrange
        GameShopException exceptionZero = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(115, 116, 0);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exceptionZero.getStatus());
        assertEquals("Quantity must be at least 1.", exceptionZero.getMessage());

        // Act & Assert for negative quantity
        GameShopException exceptionNegative = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(41, 116, -5);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exceptionNegative.getStatus());
        assertEquals("Quantity must be at least 1.", exceptionNegative.getMessage());

        // Verify that methods were not called
        verify(cartRepository, never()).findById(anyInt());
        verify(gameRepository, never()).findById(anyInt());
    }

    @Test
    public void testAddGameToCartWhenGameIsOutOfStockAfterInitialAdd() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(2901);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(2902);

        when(cartRepository.findById(2901)).thenReturn(cart);
        when(gameRepository.findById(2902)).thenReturn(game);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Add the game to the cart initially with sufficient stock
        cartService.addGameToCart(2901, 2902, 3);

        // Now set the game's stock to zero to simulate it going out of stock
        game.setStockQuantity(0);
        when(gameRepository.findById(2902)).thenReturn(game);

        // Act & Assert: Trying to increase the quantity should fail
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.addGameToCart(2901, 2902, 2);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Only 0 units of Game ID 2902 are available", exception.getMessage());

        verify(cartRepository, times(2)).findById(2901); // Called twice: one for initial add and another for second add
                                                         // attempt
        verify(gameRepository, atLeast(1)).findById(2902);
    }

    // --- Tests for removeGameFromCart ---

    @Test
    public void testRemoveGameFromCartValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(14);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(21);

        // Mock the repository calls
        when(cartRepository.findById(14)).thenReturn(cart);
        when(gameRepository.findById(21)).thenReturn(game);
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Simulate adding the game to the cart first
        cartService.addGameToCart(14, 21, 5);

        // Act
        Cart updatedCart = cartService.removeGameFromCart(14, 21, 2);

        // Assert
        assertNotNull(updatedCart);
        Map<Integer, Integer> updatedQuantities = cartService.getQuantitiesForCart(14);
        assertEquals(3, updatedQuantities.get(21));

        verify(cartRepository, atLeast(1)).findById(14);
        verify(gameRepository, atLeast(1)).findById(21);
        verify(cartRepository, atLeast(1)).save(cart);
    }

    @Test
    public void testRemoveGameFromCartGameNotInCart() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(1712);
        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(1713);
        when(cartRepository.findById(1712)).thenReturn(cart);
        when(gameRepository.findById(1713)).thenReturn(game);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.removeGameFromCart(1712, 1713, 1);
        });

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game is not in the cart.", exception.getMessage());

        verify(cartRepository, times(1)).findById(1712);
        verify(gameRepository, times(1)).findById(1713);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testRemoveGameFromCartRemovingMoreThanExists() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(1812);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(1813);

        when(cartRepository.findById(1812)).thenReturn(cart);
        when(gameRepository.findById(1813)).thenReturn(game);

        // Simulate adding the game to the cart first
        cartService.addGameToCart(1812, 1813, 2);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.removeGameFromCart(1812, 1813, 3);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cannot remove more than the existing quantity.", exception.getMessage());

        verify(cartRepository, atLeast(1)).findById(1812);
        verify(gameRepository, atLeast(1)).findById(1813);
    }

    @Test
    public void testRemoveGameFromCartInvalidQuantity() {
        Cart cart = new Cart();
        cart.setCart_id(134);

        // Act & Assert for zero quantity
        GameShopException exceptionZero = assertThrows(GameShopException.class, () -> {
            cartService.removeGameFromCart(134, 135, 0);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exceptionZero.getStatus());
        assertEquals("Quantity must be at least 1.", exceptionZero.getMessage());

        // Act & Assert for negative quantity
        GameShopException exceptionNegative = assertThrows(GameShopException.class, () -> {
            cartService.removeGameFromCart(134, 135, -3);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exceptionNegative.getStatus());
        assertEquals("Quantity must be at least 1.", exceptionNegative.getMessage());

        verify(cartRepository, never()).findById(anyInt());
        verify(gameRepository, never()).findById(anyInt());
    }

    @Test
    public void testRemoveGameFromCartCompleteRemoval() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(3001);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(3002);

        when(cartRepository.findById(3001)).thenReturn(cart);
        when(gameRepository.findById(3002)).thenReturn(game);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.addGameToCart(3001, 3002, 3);

        // Remove the game with a quantity that makes the total zero
        Cart updatedCart = cartService.removeGameFromCart(3001, 3002, 3);

        assertNotNull(updatedCart);
        assertFalse(updatedCart.getGames().contains(game),
                "Game should be removed from cart when quantity reaches zero");
        assertTrue(cartService.getQuantitiesForCart(3001).isEmpty(),
                "Quantities should be empty after complete removal");

        verify(cartRepository, times(2)).findById(3001); // Initial add and removal check
        verify(gameRepository, atLeast(1)).findById(3002);
        verify(cartRepository, times(2)).save(cart); // One for add, one for remove
    }

    // --- Tests for updateGameQuantityInCart ---

    @Test
    public void testUpdateGameQuantityInCartValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(7301);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(7302);

        when(cartRepository.findById(7301)).thenReturn(cart);
        when(gameRepository.findById(7302)).thenReturn(game);
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Simulate adding the game to the cart first
        cartService.addGameToCart(7301, 7302, 5);

        // Act
        Cart updatedCart = cartService.updateGameQuantityInCart(7301, 7302, 7);

        // Assert
        assertNotNull(updatedCart);
        Map<Integer, Integer> updatedQuantities = cartService.getQuantitiesForCart(7301);
        assertEquals(7, updatedQuantities.get(7302));

        verify(cartRepository, atLeast(1)).findById(7301);
        verify(gameRepository, atLeast(1)).findById(7302);
        verify(cartRepository, atLeast(1)).save(cart);
    }

    @Test
    public void testUpdateGameQuantityInCartGameNotInCart() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(2132);
        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(2131);
        when(cartRepository.findById(2132)).thenReturn(cart);
        when(gameRepository.findById(2131)).thenReturn(game);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.updateGameQuantityInCart(2132, 2131, 5);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Game is not in the cart.", exception.getMessage());

        verify(cartRepository, times(1)).findById(2132);
        verify(gameRepository, times(1)).findById(2131);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testUpdateGameQuantityInCartExceedsStockQuantity() {
        Cart cart = new Cart();
        cart.setCart_id(2214);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 5, "photoUrl");
        game.setGame_id(22141);

        when(cartRepository.findById(2214)).thenReturn(cart);
        when(gameRepository.findById(22141)).thenReturn(game);

        // Simulate adding the game to the cart first
        cartService.addGameToCart(2214, 22141, 2);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.updateGameQuantityInCart(2214, 22141, 10);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(
                String.format("Only %d units of Game ID %d are available", game.getStockQuantity(), 22141),
                exception.getMessage());

        verify(cartRepository, atLeast(1)).findById(2214);
        verify(gameRepository, atLeast(1)).findById(22141);
    }

    @Test
    public void testUpdateGameQuantityInCartToZero() {
        Cart cart = new Cart();
        cart.setCart_id(119);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(121);

        when(cartRepository.findById(119)).thenReturn(cart);
        when(gameRepository.findById(121)).thenReturn(game);

        // Simulate adding the game to the cart
        cartService.addGameToCart(119, 121, 5);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.updateGameQuantityInCart(119, 121, 0);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Quantity must be 1 or greater.", exception.getMessage());

        verify(cartRepository, times(1)).findById(119);
        verify(gameRepository, times(1)).findById(121);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void testUpdateGameQuantityInCartNegativeQuantity() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(117);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(118);

        when(cartRepository.findById(117)).thenReturn(cart);
        when(gameRepository.findById(118)).thenReturn(game);

        // Simulate adding the game to the cart
        cartService.addGameToCart(117, 118, 5);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.updateGameQuantityInCart(117, 118, -1);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Quantity must be 1 or greater.", exception.getMessage());

        verify(cartRepository, times(1)).findById(117);
        verify(gameRepository, times(1)).findById(118);
        verify(cartRepository, times(1)).save(cart);
    }

    // --- Tests for getAllGamesFromCartWithQuantities ---

    @Test
    public void testGetAllGamesFromCartWithQuantitiesValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(19);

        Game game1 = new Game("Title1", "Description1", 50, GameStatus.InStock, 10, "photoUrl1");
        game1.setGame_id(891);
        Game game2 = new Game("Title2", "Description2", 60, GameStatus.InStock, 15, "photoUrl2");
        game2.setGame_id(891 + 1);

        when(cartRepository.findById(19)).thenReturn(cart);
        when(gameRepository.findById(anyInt())).thenAnswer(invocation -> {
            int gameId = invocation.getArgument(0);
            if (gameId == 891) {
                return game1;
            } else if (gameId == 891 + 1) {
                return game2;
            }
            return null;
        });
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Simulate adding games to the cart
        cartService.addGameToCart(19, game1.getGame_id(), 3);
        cartService.addGameToCart(19, game2.getGame_id(), 5);

        Map<Game, Integer> result = cartService.getAllGamesFromCartWithQuantities(19);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(3, result.get(game1));
        assertEquals(5, result.get(game2));

        verify(cartRepository, atLeast(1)).findById(19);
    }

    @Test
    public void testGetAllGamesFromCartWithQuantitiesEmptyCart() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(2332);

        when(cartRepository.findById(2332)).thenReturn(cart);

        // Act
        Map<Game, Integer> result = cartService.getAllGamesFromCartWithQuantities(2332);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(cartRepository, times(1)).findById(2332);
    }

    @Test
    public void testGetAllGamesFromCartWithQuantitiesInvalidCartId() {
        when(cartRepository.findById(2333)).thenReturn(null);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.getAllGamesFromCartWithQuantities(2333);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Cart with ID %d does not exist", 2333), exception.getMessage());

        verify(cartRepository, times(1)).findById(2333);
    }

    @Test
    public void testGetAllGamesFromCartWithQuantitiesGameNotFound() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(2334);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(2335);

        when(cartRepository.findById(2334)).thenReturn(cart);

        // Simulate adding the game to the cart
        when(gameRepository.findById(2335)).thenReturn(game);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        cartService.addGameToCart(2334, 2335, 2);

        // Now, simulate that the game is no longer in the repository
        when(gameRepository.findById(2335)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.getAllGamesFromCartWithQuantities(2334);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Game with ID %d does not exist", 2335), exception.getMessage());

        verify(cartRepository, atLeast(1)).findById(2334);
        verify(gameRepository, atLeast(2)).findById(2335);
    }

    @Test
    public void testGetAllGamesFromCartWithQuantitiesWithRemovedGame() {
        Cart cart = new Cart();
        cart.setCart_id(2336);

        Game existingGame = new Game("Existing Game", "Desc", 50, GameStatus.InStock, 10, "photoUrl");
        existingGame.setGame_id(2337);

        int removedGameId = 2338;

        when(cartRepository.findById(2336)).thenReturn(cart);
        when(gameRepository.findById(existingGame.getGame_id())).thenReturn(existingGame);
        when(gameRepository.findById(removedGameId)).thenReturn(null);

        // Add the existing game to the cart using the service method
        cartService.addGameToCart(2336, existingGame.getGame_id(), 2);

        // Add the removed game's quantity to simulate its previous presence
        cartService.getQuantitiesForCart(2336).put(removedGameId, 3);

        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.getAllGamesFromCartWithQuantities(2336);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Game with ID %d does not exist", removedGameId), exception.getMessage());

        verify(cartRepository, times(2)).findById(2336);
        verify(gameRepository, times(2)).findById(existingGame.getGame_id());
        verify(gameRepository, times(1)).findById(removedGameId);
    }

    // --- Tests for getGameFromCart ---

    @Test
    public void testGetGameFromCartValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(29);

        Game game = new Game("Title", "Description", 50, GameStatus.InStock, 10, "photoUrl");
        game.setGame_id(283);

        when(cartRepository.findById(29)).thenReturn(cart);
        when(gameRepository.findById(283)).thenReturn(game);

        // Simulate adding the game to the cart
        cartService.addGameToCart(29, 283, 1);

        // Act
        Game resultGame = cartService.getGameFromCart(29, 283);

        // Assert
        assertNotNull(resultGame);
        assertEquals(283, resultGame.getGame_id());

        verify(cartRepository, atLeast(1)).findById(29);
    }

    @Test
    public void testGetGameFromCartInvalidCartId() {
        // Arrange
        when(cartRepository.findById(137)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.getGameFromCart(137, 138);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Cart with ID %d does not exist", 137), exception.getMessage());

        verify(cartRepository, times(1)).findById(137);
    }

    // --- Tests for clearCart ---

    @Test
    public void testClearCartValid() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(279);

        Game game1 = new Game("Title1", "Description1", 50, GameStatus.InStock, 10, "photoUrl1");
        game1.setGame_id(341);
        Game game2 = new Game("Title2", "Description2", 60, GameStatus.InStock, 15, "photoUrl2");
        game2.setGame_id(342);

        when(cartRepository.findById(279)).thenReturn(cart);
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        when(gameRepository.findById(341)).thenReturn(game1);
        when(gameRepository.findById(342)).thenReturn(game2);

        // Simulate adding games to the cart
        cartService.addGameToCart(279, game1.getGame_id(), 3);
        cartService.addGameToCart(279, game2.getGame_id(), 5);

        // Act
        Cart clearedCart = cartService.clearCart(279);

        // Assert
        assertNotNull(clearedCart);
        assertTrue(clearedCart.getGames().isEmpty());
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(279);
        assertTrue(quantities.isEmpty());

        verify(cartRepository, atLeast(1)).findById(279);
        verify(cartRepository, atLeast(1)).save(cart);
    }

    @Test
    public void testClearCartInvalidCartId() {
        // Arrange
        when(cartRepository.findById(9173)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.clearCart(9173);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(String.format("Cart with ID %d does not exist", 9173), exception.getMessage());

        verify(cartRepository, times(1)).findById(9173);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    public void testClearCartAlreadyEmpty() {
        Cart cart = new Cart();
        cart.setCart_id(2873);

        when(cartRepository.findById(2873)).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Ensure the cart is empty by default
        assertTrue(cart.getGames().isEmpty(), "Cart should start as empty");

        // Act: Attempt to clear an already empty cart
        Cart clearedCart = cartService.clearCart(2873);

        assertNotNull(clearedCart);
        assertTrue(clearedCart.getGames().isEmpty(), "Cart should remain empty after clear operation");
        assertTrue(cartService.getQuantitiesForCart(2873).isEmpty(),
                "Quantities should remain empty after clear operation");

        verify(cartRepository, times(1)).findById(2873);
        verify(cartRepository, times(1)).save(cart);
    }

    // --- Tests for getAllCarts ---

    @Test
    public void testGetAllCarts() {
        // Arrange
        Cart cart1 = new Cart();
        cart1.setCart_id(652);
        Cart cart2 = new Cart();
        cart2.setCart_id(653);

        List<Cart> carts = Arrays.asList(cart1, cart2);

        when(cartRepository.findAll()).thenReturn(carts);

        // Act
        Iterable<Cart> result = cartService.getAllCarts();

        // Assert
        assertNotNull(result);
        List<Cart> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(cart1));
        assertTrue(resultList.contains(cart2));

        verify(cartRepository, times(1)).findAll();
    }

    @Test
    public void testGetInvalidGameFromCart() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(16789);
        when(cartRepository.findById(16789)).thenReturn(cart);

        // Act
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.getGameFromCart(16789, 178);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 178 does not exist in the cart", exception.getMessage());

        verify(cartRepository, times(1)).findById(16789);
    }

    @Test
    public void testUpdateInvalidGameQUanityInCart() {
        // Arrange
        Cart cart = new Cart();
        cart.setCart_id(167810);
        when(cartRepository.findById(167810)).thenReturn(cart);

        // Act
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            cartService.updateGameQuantityInCart(167810, 1789, 13);
        });

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Game with ID 1789 does not exist", exception.getMessage());

        verify(cartRepository, times(1)).findById(167810);
    }
}
