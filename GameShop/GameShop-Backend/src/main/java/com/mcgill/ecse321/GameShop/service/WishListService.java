package com.mcgill.ecse321.GameShop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.WishList;
import com.mcgill.ecse321.GameShop.repository.CustomerRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.WishListRepository;

import jakarta.transaction.Transactional;

@Service
public class WishListService {
    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // Find a wishlist by its ID
    @Transactional
    public WishList findWishlistById(int id) {
        if (id <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Wishlist Id must be greater than 0");
        }
        WishList wishList = wishListRepository.findById(id);
        if (wishList == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("There is no WishList with Id %d.", id));
        }
        return wishList;
    }

    // Create a new wishlist for a customer
    @Transactional
    public WishList createWishlist(String customerEmail, String title) {
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Customer email cannot be empty or null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Wishlist title cannot be empty or null");
        }

        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("There is no customer with email: %s", customerEmail));
        }
        WishList wishList = new WishList(title, customer);
        return wishListRepository.save(wishList);
    }

    // Add a game to a wishlist
    @Transactional
    public WishList addGameToWishlist(int wishlistId, int gameId) {
        if (gameId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game Id must be greater than 0");
        }
        WishList wishList = findWishlistById(wishlistId);
        List<Game> games = wishList.getGames();
        for (Game game : games) {
            if (game.getGame_id() == gameId) {
                throw new GameShopException(HttpStatus.BAD_REQUEST, "Game is already in the wishlist");
            }
        }
        Game gameSearch = gameRepository.findById(gameId);
        if (gameSearch == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("There is no Game with Id %d.", gameId));
        }

        wishList.addGame(gameSearch);
        return wishListRepository.save(wishList);
    }

    // Remove a game from a wishlist
    @Transactional
    public WishList removeGameFromWishlist(int wishlistId, int gameId) {
        if (gameId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game Id must be greater than 0");
        }
        WishList wishList = findWishlistById(wishlistId);

        List<Game> games = wishList.getGames();
        for (Game game : games) {
            if (game.getGame_id() == gameId) {
                wishList.removeGame(game);
                return wishListRepository.save(wishList);
            }
        }
        throw new GameShopException(HttpStatus.NOT_FOUND,
                String.format("There is no Game with Id %d in the WishList with Id %d.", gameId, wishlistId));
    }

    // Get the size of a wishlist
    @Transactional
    public int getWishlistSize(int wishlistId) {
        WishList wishList = findWishlistById(wishlistId);
        return wishList.getGames().size();
    }

    // Get all games in a wishlist
    @Transactional
    public List<Game> getGamesInWishList(int wishlistId) {
        if (wishlistId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Wishlist Id must be greater than 0");
        }
        WishList wishList = findWishlistById(wishlistId);
        return wishList.getGames();
    }

    // Remove all games from a wishlist
    @Transactional
    public WishList removeAllGamesFromWishlist(int wishlistId) {
        if (wishlistId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Wishlist Id must be greater than 0");
        }
        WishList wishList = findWishlistById(wishlistId);
        wishList.setGames(new ArrayList<>());
        return wishListRepository.save(wishList);
    }

    // Get a specific game from a wishlist
    @Transactional
    public Game getGameInWishList(int wishlistId, int gameId) {
        if (gameId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game Id must be greater than 0");
        }
        WishList wishList = findWishlistById(wishlistId);
        Iterable<Game> games = wishList.getGames();
        for (Game game : games) {
            if (game.getGame_id() == gameId) {
                return game;
            }
        }
        throw new GameShopException(HttpStatus.NOT_FOUND,
                String.format("There is no Game with Id %d in the WishList with Id %d.", gameId, wishlistId));
    }

    @Transactional
    public WishList findWishlistByCustomerEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Customer email cannot be empty or null");
        }

        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("No customer found with email: %s", email));
        }

        WishList wishList = wishListRepository.findByCustomer(customer);
        if (wishList == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("No wishlist found for customer with email: %s", email));
        }

        return wishList;
    }

}