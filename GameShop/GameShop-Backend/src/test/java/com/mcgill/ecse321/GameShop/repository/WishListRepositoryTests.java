package com.mcgill.ecse321.GameShop.repository;

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
import com.mcgill.ecse321.GameShop.model.WishList;

import jakarta.transaction.Transactional;

@SpringBootTest
public class WishListRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // Clear the database before and after each test
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        wishListRepository.deleteAll();
        wishListRepository.deleteAll();
        gameRepository.deleteAll();
        customerRepository.deleteAll();
        cartRepository.deleteAll();
        Account.clearTestEmails(WishListRepositoryTests.testEmails);
        WishListRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadWishList() {
        // Create and save a Cart
        Cart cart = new Cart();
        cart = cartRepository.save(cart);

        // Create and save a Customer
        String email = "moh.abdelhady@moh.com";
        String username = "Marcg";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9277";
        String address = "120 rue Pen";
        Customer aCustomer = new Customer(email, username, password, phoneNumber, address, cart);
        aCustomer = customerRepository.save(aCustomer);
        WishListRepositoryTests.testEmails.add(aCustomer.getEmail());

        // Create and save a WishList
        String aTitle = "Marc's wishlist";
        WishList aWishList = new WishList(aTitle, aCustomer);

        // Create and save Games
        Game game1 = new Game("Title", "Game 1 Description", 50, Game.GameStatus.InStock, 10, "http://photo1.url");
        Game game2 = new Game("Title", "Game 2 Description", 60, Game.GameStatus.InStock, 5, "http://photo2.url");
        game1 = gameRepository.save(game1);
        int game1Id = game1.getGame_id();
        game2 = gameRepository.save(game2);
        int game2Id = game2.getGame_id();

        // Associate games with the WishList and save it
        aWishList.addGame(game1);
        aWishList.addGame(game2);
        aWishList = wishListRepository.save(aWishList);
        int wishList_id = aWishList.getWishList_id();

        // Retrieve and verify the WishList
        WishList pulledWishList = wishListRepository.findById(wishList_id);
        assertNotNull(pulledWishList);
        assertEquals(aTitle, pulledWishList.getTitle());

        // Verify associated Customer details
        Customer pulledCustomer = pulledWishList.getCustomer();
        assertNotNull(pulledCustomer);
        assertEquals(email, pulledCustomer.getEmail());
        assertEquals(username, pulledCustomer.getUsername());
        assertEquals(password, pulledCustomer.getPassword());
        assertEquals(phoneNumber, pulledCustomer.getPhoneNumber());
        assertEquals(address, pulledCustomer.getAddress());

        // Verify associated Games
        List<Game> gameList = pulledWishList.getGames();
        assertNotNull(gameList);
        assertEquals(2, gameList.size());

        // Check if game1 is in the WishList
        boolean wasFound = false;
        for (Game game : gameList) {
            if (game.getGame_id() == game1Id) {
                wasFound = true;
                break;
            }
        }
        assertTrue(wasFound);

        // Check if game2 is in the WishList
        wasFound = false;
        for (Game game : gameList) {
            if (game.getGame_id() == game2Id) {
                wasFound = true;
                break;
            }
        }
        assertTrue(wasFound);
    }

    @Test
    @Transactional
    public void testUpdateWishListDetails() {
        // Create and save a Cart
        Cart cart = new Cart();
        cart = cartRepository.save(cart);

        // Create and save a Customer
        Customer customer = new Customer("moh.abdelhady@moh.com", "mohamed", "password", "+1 (123) 123-4567",
                "132 Drummond", cart);
        customer = customerRepository.save(customer);
        WishListRepositoryTests.testEmails.add(customer.getEmail());

        // Create and save a WishList
        WishList wishList = new WishList("Mohamed's WishList", customer);
        wishList = wishListRepository.save(wishList);

        // Update WishList title and save it
        wishList.setTitle("Mohamed's Updated WishList");
        wishList = wishListRepository.save(wishList);

        // Retrieve and verify the updated WishList
        WishList updatedWishList = wishListRepository.findById(wishList.getWishList_id());
        assertNotNull(updatedWishList);
        assertEquals("Mohamed's Updated WishList", updatedWishList.getTitle(), "WishList title should be updated.");
    }
}