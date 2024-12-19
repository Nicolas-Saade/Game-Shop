package com.mcgill.ecse321.GameShop.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mcgill.ecse321.GameShop.dto.CartDto.CartListDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartRequestDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartResponseDto;
import com.mcgill.ecse321.GameShop.dto.CartDto.CartSummaryDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.service.CartService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * Retrieves the cart associated with the given customer email.
     *
     * @param customerEmail the email of the customer whose cart is to be retrieved
     * @return a CartResponseDto containing the cart details and item quantities
     */
    @GetMapping("/carts/customer/{customerEmail}")
    public CartResponseDto getCartByCustomerEmail(@PathVariable String customerEmail) {
        // get the cart associated with the customer
        Cart cart = cartService.getCartByCustomerEmail(customerEmail);

        // Get the quantities of each game in the cart
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cart.getCart_id());
        return CartResponseDto.create(cart, quantities);
    }

    /**
     * Adds a game to the specified cart.
     *
     * @param cartId     the ID of the cart to which the game will be added
     * @param requestDto the request data transfer object containing the game ID and
     *                   quantity
     * @return a response data transfer object representing the updated cart
     */
    @PutMapping("/carts/{cartId}/games")
    public CartResponseDto addGameToCart(
            @PathVariable int cartId,
            @Valid @RequestBody CartRequestDto requestDto) {
        int gameId = requestDto.getGameId();
        int quantity = requestDto.getQuantity();

        // Add the game to the cart
        cartService.addGameToCart(cartId, gameId, quantity);

        // Get the cart by its id
        Cart cart = cartService.getCartById(cartId);
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cartId);
        return CartResponseDto.create(cart, quantities);
    }

    /**
     * Removes a specified quantity of a game from a cart.
     *
     * @param cartId     the ID of the cart from which the game will be removed
     * @param requestDto the request data transfer object containing the game ID and
     *                   quantity to be removed
     * @return a CartResponseDto containing the updated cart and its game quantities
     */
    @PutMapping("/carts/{cartId}/games/remove")
    public CartResponseDto removeGameFromCart(
            @PathVariable int cartId,
            @Valid @RequestBody CartRequestDto requestDto) {
        int gameId = requestDto.getGameId();
        int quantity = requestDto.getQuantity();

        // call service method to remove game from cart
        cartService.removeGameFromCart(cartId, gameId, quantity);

        // get the cart by its id and get quantities
        Cart cart = cartService.getCartById(cartId);
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cartId);
        return CartResponseDto.create(cart, quantities);
    }

    /**
     * Retrieves a cart by its ID.
     *
     * @param cartId the ID of the cart to retrieve
     * @return a CartResponseDto containing the cart details and item quantities
     */
    @GetMapping("/carts/{cartId}")
    public CartResponseDto findCartById(@PathVariable int cartId) {
        Cart cart = cartService.getCartById(cartId);
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cartId);
        return CartResponseDto.create(cart, quantities);
    }

    /**
     * Handles GET requests to retrieve all carts.
     * 
     * @return a CartListDto containing a list of CartSummaryDto objects, each
     *         representing a summary of a cart.
     *         The summary includes the cart details, total number of items, and
     *         total price.
     */
    @GetMapping("/carts")
    public CartListDto findAllCarts() {
        // Get all carts
        List<Cart> carts = (List<Cart>) cartService.getAllCarts();

        // calculate total items and total price for each cart
        List<CartSummaryDto> cartSummaries = carts.stream()
                .map(cart -> {
                    int cartId = cart.getCart_id();
                    Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cartId);
                    int totalItems = quantities.values().stream().mapToInt(Integer::intValue).sum();
                    double totalPrice = cart.getGames().stream()
                            .mapToDouble(game -> game.getPrice() * quantities.getOrDefault(game.getGame_id(), 1))
                            .sum();
                    return new CartSummaryDto(cart, totalItems, totalPrice);
                })
                .collect(Collectors.toList());
        return new CartListDto(cartSummaries);
    }

    /**
     * Updates the quantity of a specific game in a cart.
     *
     * @param cartId     the ID of the cart to update
     * @param gameId     the ID of the game whose quantity is to be updated
     * @param requestDto the request body containing the new quantity
     * @return a CartResponseDto containing the updated cart information
     */
    @PutMapping("/carts/{cartId}/games/{gameId}/quantity")
    public CartResponseDto updateGameQuantityInCart(
            @PathVariable int cartId,
            @PathVariable int gameId,
            @Valid @RequestBody CartRequestDto requestDto) {
        int quantity = requestDto.getQuantity();

        // Update the quantity of the game in the cart by calling service method
        cartService.updateGameQuantityInCart(cartId, gameId, quantity);

        Cart cart = cartService.getCartById(cartId);
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cartId);
        return CartResponseDto.create(cart, quantities);
    }

    /**
     * Clears the contents of the cart with the specified ID.
     *
     * @param cartId the ID of the cart to be cleared
     * @return a CartResponseDto containing the updated cart information and item
     *         quantities
     */
    @PutMapping("/carts/{cartId}/clear")
    public CartResponseDto clearCart(@PathVariable int cartId) {
        // call service method to clear the cart
        cartService.clearCart(cartId);
        Cart cart = cartService.getCartById(cartId);

        // get the cart by its id and get quantities
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cartId);
        return CartResponseDto.create(cart, quantities);
    }

    /**
     * Retrieves a specific game from a cart.
     *
     * @param cartId the ID of the cart
     * @param gameId the ID of the game to retrieve
     * @return a GameResponseDto containing the details of the retrieved game
     */
    @GetMapping("/carts/{cartId}/games/{gameId}")
    public GameResponseDto getGameFromCart(
            @PathVariable int cartId,
            @PathVariable int gameId) {
        Game game = cartService.getGameFromCart(cartId, gameId);
        return GameResponseDto.create(game);
    }

    /**
     * Retrieves a list of games in the specified cart.
     *
     * @param cartId the ID of the cart from which to retrieve games
     * @return a GameListDto containing a list of GameSummaryDto objects
     *         representing the games in the cart
     */
    @GetMapping("/carts/{cartId}/games")
    public GameListDto getGamesInCart(@PathVariable int cartId) {
        Map<Game, Integer> gamesWithQuantities = cartService.getAllGamesFromCartWithQuantities(cartId);

        // Create a list of GameSummaryDto objects from the games in the cart using the
        // game quantities
        List<GameSummaryDto> gameSummaries = gamesWithQuantities.entrySet().stream()
                .map(entry -> new GameSummaryDto(entry.getKey()))
                .collect(Collectors.toList());
        return new GameListDto(gameSummaries);
    }
}