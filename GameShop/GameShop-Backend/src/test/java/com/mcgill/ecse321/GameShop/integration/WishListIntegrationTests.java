package com.mcgill.ecse321.GameShop.integration;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountRequestDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountResponseDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameRequestDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.dto.WishListDto.WishListResponseDto;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.WishList;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CustomerRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.WishListRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WishListIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private WishListRepository wishListRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    private static final String CUSTOMER_EMAIL = "customerforintegrationst@example.com";
    private static final String CUSTOMER_USERNAME = "customerUser";
    private static final String CUSTOMER_PASSWORD = "customerPass";
    private static final String CUSTOMER_PHONE = "123-456-7890";
    private static final String CUSTOMER_ADDRESS = "123 Customer Street";

    private int wishListId;
    private int gameId1;
    private int gameId2;
    private int gameId3;

    @BeforeAll
    @AfterAll
    public void clearDatabase() {
        wishListRepo.deleteAll();
        gameRepo.deleteAll();
        customerRepo.deleteAll();
        accountRepo.deleteAll();
    }

    /*** Helper Method to Retrieve Wishlist ID by Customer Email ***/
    private int getWishlistIdByCustomerEmail(String email) {
        Customer customer = customerRepo.findByEmail(email);
        assertNotNull(customer, "Customer should exist");
        WishList wishList = wishListRepo.findByCustomer(customer);
        assertNotNull(wishList, "Wishlist should be created automatically for the customer");
        return wishList.getWishList_id();
    }

    /*** Create Customer (Implicitly Creates Wishlist) ***/

    @Test
    @Order(1)
    public void testCreateCustomerAndImplicitlyCreateWishList() {
        // Create customer
        AccountRequestDto customerDto = new AccountRequestDto(
                CUSTOMER_EMAIL, CUSTOMER_USERNAME, CUSTOMER_PASSWORD, CUSTOMER_PHONE, CUSTOMER_ADDRESS);
        ResponseEntity<AccountResponseDto> customerResponse = client.postForEntity(
                "/account/customer", customerDto, AccountResponseDto.class);

        assertNotNull(customerResponse);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        AccountResponseDto customer = customerResponse.getBody();
        assertNotNull(customer);
        assertEquals(CUSTOMER_EMAIL, customer.getEmail());

        // Retrieve wishlist ID associated with the customer
        this.wishListId = getWishlistIdByCustomerEmail(CUSTOMER_EMAIL);
        assertTrue(this.wishListId > 0, "Wishlist ID should be greater than 0");
        assertNotNull(wishListRepo.findById(this.wishListId));
    }

    @Test
    @Order(2)
    public void testFindWishlistById() {
        assertNotNull(wishListRepo.findById(this.wishListId), "Wishlist should exist");
        String url = String.format("/wishlists/%d", this.wishListId); // Correct path
        ResponseEntity<WishListResponseDto> response = client.getForEntity(url, WishListResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        WishListResponseDto wishList = response.getBody();
        assertNotNull(wishList);
        assertEquals(this.wishListId, wishList.getWishlistId());
        assertEquals("customer", wishList.getTitle());
        assertEquals(CUSTOMER_EMAIL, wishList.getCustomerEmail());
    }

    @Test
    @Order(3)
    public void testFindWishlistByNonExistentId() {
        int nonExistentId = 9999;
        String url = String.format("/wishlists/%d", nonExistentId);
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("There is no WishList with Id"));
    }

    // /*** Add Game to Wishlist ***/

    @Test
    @Order(6)
    public void testAddGameToWishlist() {
        // Create a game
        GameRequestDto gameRequest = new GameRequestDto(
                "Test Game", "A test game", 59, GameStatus.InStock, 10, "www.testgame.com");
        ResponseEntity<GameResponseDto> gameResponse = client.postForEntity(
                "/games", gameRequest, GameResponseDto.class);

        assertNotNull(gameResponse);
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
        GameResponseDto game = gameResponse.getBody();
        assertNotNull(game);
        assertTrue(game.getaGame_id() > 0);
        this.gameId1 = game.getaGame_id();

        // Add game to wishlist
        String url = String.format("/wishlist/%d/%d", this.wishListId, this.gameId1);
        ResponseEntity<WishListResponseDto> response = client.exchange(
                url, HttpMethod.PUT, null, WishListResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        WishListResponseDto wishList = response.getBody();
        assertNotNull(wishList);
        assertTrue(wishList.getGames().getGames().stream().anyMatch(g -> g.getGameId() == this.gameId1));
    }

    @Test
    @Order(7)
    public void testAddGameToWishlistAlreadyInWishlist() {
        String url = String.format("/wishlist/%d/%d", this.wishListId, this.gameId1);
        ResponseEntity<String> response = client.exchange(url, HttpMethod.PUT, null, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Game is already in the wishlist"));
    }

    // /*** Get All Games in Wishlist ***/

    @Test
    @Order(8)
    public void testGetAllGamesInWishlist() {
        GameRequestDto gameRequest2 = new GameRequestDto(
                "Test Game 2 ", "A test game2", 59, GameStatus.InStock, 10, "www.testgame.com");
        ResponseEntity<GameResponseDto> gameResponse2 = client.postForEntity(
                "/games", gameRequest2, GameResponseDto.class);

        assertNotNull(gameResponse2);
        assertEquals(HttpStatus.OK, gameResponse2.getStatusCode(), "Failed to create game 2");
        GameResponseDto game2 = gameResponse2.getBody();
        assertNotNull(game2);
        assertTrue(game2.getaGame_id() > 0);
        this.gameId2 = game2.getaGame_id();

        // Add game to wishlist
        String url = String.format("/wishlist/%d/%d", this.wishListId, this.gameId2);
        ResponseEntity<WishListResponseDto> response = client.exchange(
                url, HttpMethod.PUT, null, WishListResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Failed to add game to wishlist");
        WishListResponseDto wishList = response.getBody();
        assertNotNull(wishList);
        assertTrue(wishList.getGames().getGames().stream().anyMatch(g -> g.getGameId() == this.gameId2));

        String url2 = String.format("/wishlist/%d/", this.wishListId);
        ResponseEntity<GameListDto> response2 = client.getForEntity(url2, GameListDto.class);

        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response2.getStatusCode(), "Failed to get games in wishlist");
        GameListDto games = response2.getBody();
        assertNotNull(games);
        assertTrue(games.getGames().stream().anyMatch(g -> g.getGameId() == this.gameId1));
        assertTrue(games.getGames().stream().anyMatch(g -> g.getGameId() == this.gameId2));
    }

    @Test
    @Order(14)
    public void testGetAllGamesInWishlistEmpty() {
        // Remove all games first

        String url = String.format("/wishlist/%d/", this.wishListId);
        ResponseEntity<GameListDto> response = client.getForEntity(url, GameListDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameListDto games = response.getBody();
        assertNotNull(games);
        assertTrue(games.getGames().isEmpty(), "Expected no games in the wishlist");
    }

    // /*** Get Specific Game in Wishlist ***/

    @Test
    @Order(9)
    public void testGetGameInWishlist() {
        String url = String.format("/wishlist/%d/%d", this.wishListId, this.gameId1);
        ResponseEntity<GameResponseDto> response = client.getForEntity(url, GameResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GameResponseDto game = response.getBody();
        assertNotNull(game);
        assertEquals(this.gameId1, game.getaGame_id());
    }

    @Test
    @Order(10)
    public void testGetGameInWishlistNotInWishlist() {
        // Create a new game not in wishlist
        GameRequestDto gameRequest = new GameRequestDto(
                "Another Test Game", "Another test game", 49, GameStatus.InStock, 5, "www.anothertestgame.com");
        ResponseEntity<GameResponseDto> gameResponse = client.postForEntity(
                "/games", gameRequest, GameResponseDto.class);

        assertNotNull(gameResponse);
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
        GameResponseDto game = gameResponse.getBody();
        assertNotNull(game);
        assertTrue(game.getaGame_id() > 0);
        this.gameId3 = game.getaGame_id();

        String url = String.format("/wishlist/%d/%d", this.wishListId, this.gameId3);
        ResponseEntity<String> response = client.getForEntity(url, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("There is no Game with Id"));
    }

    // /*** Remove Game from Wishlist ***/

    @Test
    @Order(11)
    public void testRemoveGameFromWishlist() {
        String url = String.format("/wishlist/%d/%d", this.wishListId, this.gameId1);
        ResponseEntity<WishListResponseDto> response = client.exchange(
                url, HttpMethod.DELETE, null, WishListResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        WishListResponseDto wishList = response.getBody();
        assertNotNull(wishList);
        assertFalse(wishList.getGames().getGames().stream().anyMatch(g -> g.getGameId() == this.gameId1));
    }

    @Test
    @Order(12)
    public void testRemoveGameFromWishlistNotInWishlist() {
        String url = String.format("/wishlist/%d/%d", this.wishListId, this.gameId1);
        ResponseEntity<String> response = client.exchange(url, HttpMethod.DELETE, null, String.class);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("There is no Game with Id"));
    }

    // /*** Remove All Games from Wishlist ***/

    @Test
    @Order(13)
    public void testRemoveAllGamesFromWishlist() {
        // Add game to wishlist
        String addUrl = String.format("/wishlist/%d/%d", this.wishListId, this.gameId3);
        ResponseEntity<WishListResponseDto> addResponse = client.exchange(
                addUrl, HttpMethod.PUT, null, WishListResponseDto.class);

        assertNotNull(addResponse);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());

        String url = String.format("/wishlist/%d", this.wishListId);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, getJsonHeaders());
        ResponseEntity<WishListResponseDto> response = client.exchange(
                url, HttpMethod.PUT, requestEntity, WishListResponseDto.class);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        WishListResponseDto wishList = response.getBody();
        assertNotNull(wishList);
        assertTrue(wishList.getGames().getGames().isEmpty(), "Expected all games to be removed from the wishlist");
    }

    /*** Helper Method to Set JSON Headers ***/
    private HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
