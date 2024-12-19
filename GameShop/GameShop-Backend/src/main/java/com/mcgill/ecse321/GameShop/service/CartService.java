package com.mcgill.ecse321.GameShop.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CartRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private AccountRepository customerRepository;

    // In-memory map to track quantities per cart - Key: cartId, Value: Map of
    // gameId to quantity
    private Map<Integer, Map<Integer, Integer>> cartQuantities = new ConcurrentHashMap<>();

    public Cart getCartByCustomerEmail(String email) {
        Customer customer = (Customer) customerRepository.findByEmail(email);
        if (customer == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        Cart cart = customer.getCart();
        return cart;
    }

    @Transactional
    public Cart getCartById(int cartId) {
        Cart cart = cartRepository.findById(cartId);
        if (cart == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Cart with ID %d does not exist", cartId));
        }
        // Initialize the quantities map for the cart if it doesn't exist
        cartQuantities.computeIfAbsent(cartId, k -> new ConcurrentHashMap<>());
        return cart;
    }

    @Transactional
    public Cart addGameToCart(int cartId, int gameId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1.");
        }
        Cart cart = getCartById(cartId);
        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Game with ID %d does not exist", gameId));
        }

        if (!game.getGameStatus().equals(Game.GameStatus.InStock)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST,
                    String.format("Game with ID %d is not available for purchase", gameId));
        }
        // Retrieve or initialize the quantities map for the cart
        Map<Integer, Integer> quantities = cartQuantities.computeIfAbsent(cart.getCart_id(),
                k -> new ConcurrentHashMap<>());
        int currentQuantity = quantities.getOrDefault(gameId, 0);
        int newQuantity = currentQuantity + quantity;
        if (newQuantity > game.getStockQuantity()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST,
                    String.format("Only %d units of Game ID %d are available", game.getStockQuantity(), gameId));
        }
        if (currentQuantity == 0) {
            cart.addGame(game);
        }
        quantities.put(gameId, newQuantity);
        cartQuantities.put(cart.getCart_id(), quantities);
        cartRepository.save(cart);

        return cart;
    }

    @Transactional
    public Cart removeGameFromCart(int cartId, int gameId, int quantity) {
        if (quantity <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1.");
        }

        Cart cart = getCartById(cartId);

        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Game with ID %d does not exist", gameId));
        }

        // Retrieve the quantities map for the cart
        Map<Integer, Integer> quantities = cartQuantities.get(cart.getCart_id());
        if (quantities == null || !quantities.containsKey(gameId)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game is not in the cart.");
        }

        int newQuantity = quantities.get(gameId) - quantity;

        if (newQuantity > 0) {
            quantities.put(gameId, newQuantity);
            cartRepository.save(cart);
        } else if (newQuantity == 0) {
            quantities.remove(gameId);
            cart.removeGame(game);
            cartRepository.save(cart);
        } else {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Cannot remove more than the existing quantity.");
        }
        cartQuantities.put(cart.getCart_id(), quantities);
        return cart;
    }

    @Transactional
    public Cart updateGameQuantityInCart(int cartId, int gameId, int quantity) {
        if (quantity <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Quantity must be 1 or greater.");
        }
        Cart cart = getCartById(cartId);
        Game game = gameRepository.findById(gameId);

        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Game with ID %d does not exist", gameId));
        }

        if (!game.getGameStatus().equals(Game.GameStatus.InStock)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST,
                    String.format("Game with ID %d is not available for purchase", gameId));
        }

        // Retrieve the quantities map for the cart
        Map<Integer, Integer> quantities = cartQuantities.get(cart.getCart_id());
        if (quantities == null || !quantities.containsKey(gameId)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game is not in the cart.");
        }

        if (quantity == 0) {
            cart.removeGame(game);
            quantities.remove(gameId);

        } else {
            if (quantity > game.getStockQuantity()) {
                throw new GameShopException(HttpStatus.BAD_REQUEST,
                        String.format("Only %d units of Game ID %d are available", game.getStockQuantity(), gameId));
            }
            quantities.put(gameId, quantity);

        }
        cartQuantities.put(cart.getCart_id(), quantities);
        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public Map<Game, Integer> getAllGamesFromCartWithQuantities(int cartId) {
        // Retrieve the Cart to ensure it exists
        Cart cart = getCartById(cartId);
        if (cart == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Cart with ID %d does not exist", cartId));
        }

        // Retrieve the map of gameId to quantity for the specified cart
        Map<Integer, Integer> quantities = cartQuantities.get(cartId);
        if (quantities == null || quantities.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Game, Integer> gamesInCart = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            int gameId = entry.getKey();
            int quantity = entry.getValue();

            Game game = gameRepository.findById(gameId);
            if (game == null) {
                throw new GameShopException(HttpStatus.NOT_FOUND,
                        String.format("Game with ID %d does not exist", gameId));
            }

            gamesInCart.put(game, quantity);
        }
        cartQuantities.put(cart.getCart_id(), quantities);
        return gamesInCart;
    }

    @Transactional
    public Game getGameFromCart(int cartId, int gameId) {
        Cart cart = getCartById(cartId);
        Game gameOpt = null;
        for (Game game : cart.getGames()) {
            if (game.getGame_id() == gameId) {
                gameOpt = game;
                break;
            }
        }
        if (gameOpt == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("Game with ID %d does not exist in the cart", gameId));
        }
        return gameOpt;
    }

    @Transactional
    public Map<Integer, Integer> getQuantitiesForCart(int cartId) {
        return cartQuantities.getOrDefault(cartId, Collections.emptyMap());
    }

    @Transactional
    public Iterable<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Transactional
    public Cart clearCart(int cartId) {
        Cart cart = getCartById(cartId);
        Map<Integer, Integer> quantities = cartQuantities.get(cartId);
        if (quantities != null) {
            quantities.clear();
        }
        cart.getGames().clear();
        cartRepository.save(cart);
        return cart;
    }
}
