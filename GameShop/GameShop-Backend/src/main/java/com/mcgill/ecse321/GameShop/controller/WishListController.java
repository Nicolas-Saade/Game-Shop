package com.mcgill.ecse321.GameShop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.WishListDto.WishListResponseDto;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.WishList;
import com.mcgill.ecse321.GameShop.service.WishListService;

@RestController
@CrossOrigin(origins = "*")
public class WishListController {

    @Autowired
    private WishListService wishListService;

    /**
     * Find a wishlist by its ID.
     * 
     * @param wishlist_id the ID of the wishlist to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved wishlist
     */
    @GetMapping("/wishlists/{wishlist_id}")
    public WishListResponseDto findWishlistById(@PathVariable int wishlist_id) {
        // Find the wishlist by its ID
        WishList wishList = wishListService.findWishlistById(wishlist_id);
        return WishListResponseDto.create(wishList);
    }

    /**
     * Add a game to a wishlist.
     * 
     * @param wishlist_id the ID of the wishlist to add the game to
     * @param game_id     the ID of the game to add to the wishlist
     * @return the response data transfer object containing the details of the
     *         updated wishlist
     */
    @PutMapping("/wishlist/{wishlist_id}/{game_id}")
    public WishListResponseDto addGameToWishlist(@PathVariable int wishlist_id, @PathVariable int game_id) {
        // Add the game to the wishlist
        WishList wishList = wishListService.addGameToWishlist(wishlist_id, game_id);
        return WishListResponseDto.create(wishList);
    }

    /**
     * Remove a game from a wishlist.
     * 
     * @param wishlist_id the ID of the wishlist to remove the game from
     * @param game_id     the ID of the game to remove from the wishlist
     * @return the response data transfer object containing the details of the
     *         updated wishlist
     */
    @DeleteMapping("/wishlist/{wishlist_id}/{game_id}")
    public WishListResponseDto removeGameFromWishlist(@PathVariable int wishlist_id, @PathVariable int game_id) {
        // Remove the game from the wishlist
        WishList wishList = wishListService.removeGameFromWishlist(wishlist_id, game_id);
        return WishListResponseDto.create(wishList);
    }

    /**
     * Remove all games from a wishlist.
     * 
     * @param wishlist_id the ID of the wishlist to remove all games from
     * @return the response data transfer object containing the details of the
     *         updated wishlist
     */
    @PutMapping("/wishlist/{wishlist_id}")
    public WishListResponseDto removeAllGamesFromWishlist(@PathVariable int wishlist_id) {
        // Find the wishlist by id and remove all games from the wishlist
        WishList wishList = wishListService.findWishlistById(wishlist_id);
        wishList = wishListService.removeAllGamesFromWishlist(wishlist_id);
        return WishListResponseDto.create(wishList);
    }

    /**
     * Get all games in a wishlist.
     * 
     * @param wishlist_id the ID of the wishlist to retrieve games from
     * @return a list data transfer object containing the summaries of all games in
     *         the wishlist
     */
    @GetMapping("/wishlist/{wishlist_id}/")
    public GameListDto getAllGamesInWishlist(@PathVariable int wishlist_id) {
        WishList wishList = wishListService.findWishlistById(wishlist_id);
        WishListResponseDto wishListResponseDto = new WishListResponseDto(wishList);
        // Return the list of games in the wishlist
        return wishListResponseDto.getGames();
    }

    /**
     * Get a specific game in a wishlist.
     * 
     * @param wishlist_id the ID of the wishlist to retrieve the game from
     * @param game_id     the ID of the game to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved game
     */
    @GetMapping("/wishlist/{wishlist_id}/{game_id}")
    public GameResponseDto getGameInWishList(@PathVariable int wishlist_id, @PathVariable int game_id) {
        Game game = wishListService.getGameInWishList(wishlist_id, game_id);
        return new GameResponseDto(game);
    }

    /**
     * Find a wishlist by customer email.
     * 
     * @param email the email of the customer
     * @return the response DTO containing the wishlist details
     */
    @GetMapping("/wishlist/customer/{email}")
    public WishListResponseDto findWishlistByCustomerEmail(@PathVariable String email) {
        WishList wishList = wishListService.findWishlistByCustomerEmail(email);
        return WishListResponseDto.create(wishList);
    }

}
