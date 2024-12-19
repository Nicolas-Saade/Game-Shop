package com.mcgill.ecse321.GameShop.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Order;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.repository.CustomerRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SpecificGameService specificGameService;

    @Autowired
    private GameService gameService;

    @Transactional
    public Order createOrder(Date orderDate, String note, String paymentCard, String customerEmail) {
        // Validate customer email
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Customer email cannot be empty or null");
        }
        if (paymentCard == null || paymentCard.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Payment card cannot be empty or null");
        }
        if (paymentCard.length() != 16) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Payment card must be 16 digits long");
        }
        if (paymentCard.matches("[0-9]+") == false) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Payment card must be a number");
        }
        // Retrieve customer by email
        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        // Retrieve customer's cart and validate it is not empty
        Cart cart = customer.getCart();
        if (cart == null || cart.getGames().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // Create new order
        Order order = new Order(orderDate, note, paymentCard, customer);
        List<Game> gamesInCart = cart.getGames();
        Map<Integer, Integer> quantities = cartService.getQuantitiesForCart(cart.getCart_id());

        // Process each game in the cart
        for (Game game : gamesInCart) {
            int gameId = game.getGame_id();
            int quantity = quantities.getOrDefault(gameId, 0);

            // Check stock quantity
            if (game.getStockQuantity() < quantity) {
                throw new GameShopException(HttpStatus.BAD_REQUEST,
                        String.format("Insufficient stock for game ID %d", gameId));
            }

            // Retrieve SpecificGame instances
            Iterable<SpecificGame> specificGames = specificGameService.getSpecificGamesByGameId(game.getGame_id());
            List<SpecificGame> specificGameList = new ArrayList<>();
            specificGames.forEach(specificGameList::add);

            // Validate enough SpecificGame instances are available
            if (specificGameList.size() < quantity) {
                throw new GameShopException(HttpStatus.BAD_REQUEST,
                        "Not enough SpecificGame instances available for game ID " + game.getGame_id());
            }

            // Add SpecificGame instances to the order
            for (int i = 0; i < quantity; i++) {
                SpecificGame specificGame = specificGameList.get(i);
                specificGame.addOrder(order);
            }

            // Update game stock quantity
            game.setStockQuantity(game.getStockQuantity() - quantity);
            gameService.updateGameStockQuantity(game.getGame_id(), game.getStockQuantity());
        }

        // Clear the cart and save the order
        cartService.clearCart(cart.getCart_id());
        return orderRepository.save(order);
    }

    @Transactional
    public Order getOrderByTrackingNumber(String trackingNumber) {
        // Retrieve order by tracking number
        Order order = orderRepository.findByTrackingNumber(trackingNumber);
        if (order == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return order;
    }

    @Transactional
    public List<Order> getAllOrders() {
        // Retrieve all orders
        return (List<Order>) orderRepository.findAll();
    }

    @Transactional
    public Order updateOrder(String trackingNumber, Date orderDate, String note, String paymentCard) {
        // Retrieve order by tracking number
        Order order = orderRepository.findByTrackingNumber(trackingNumber);
        if (order == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Order not found");
        }
        // Update order details
        order.setOrderDate(orderDate);
        order.setNote(note);
        order.setPaymentCard(paymentCard);
        return orderRepository.save(order);
    }

    @Transactional
    public void addGameToOrder(String trackingNumber, int gameId, int quantity) {
        // Retrieve the order by tracking number and validate
        Order order = getOrderByTrackingNumber(trackingNumber);
        if (order == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Order not found");
        }

        // Retrieve the game by ID and validate
        Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Game not found");
        }

        // Check if the game's stock is sufficient for the quantity requested
        if (game.getStockQuantity() < quantity) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        }

        // Retrieve all SpecificGame instances for the game, filtering to only those not
        // yet in the order
        Iterable<SpecificGame> specificGames = specificGameService.getSpecificGamesByGameId(game.getGame_id());
        List<SpecificGame> availableSpecificGames = new ArrayList<>();
        specificGames.forEach(specificGame -> {
            if (!specificGame.getOrder().contains(order)) {
                availableSpecificGames.add(specificGame);
            }
        });

        // Check if there are enough available specific games to fulfill the quantity
        // request
        if (availableSpecificGames.size() < quantity) {
            throw new GameShopException(HttpStatus.BAD_REQUEST,
                    "Not enough SpecificGame instances available for game ID " + game.getGame_id());
        }

        // Add the specified quantity of SpecificGame instances to the order
        for (int i = 0; i < quantity; i++) {
            SpecificGame specificGame = availableSpecificGames.get(i);
            specificGame.addOrder(order);
        }

        // Update stock quantity for the game and save
        game.setStockQuantity(game.getStockQuantity() - quantity);
        gameService.updateGameStockQuantity(game.getGame_id(), game.getStockQuantity());
        orderRepository.save(order);
    }

    @Transactional
    public List<SpecificGame> getSpecificGamesByOrder(String trackingNumber) {
        // Retrieve order by tracking number
        Order order = getOrderByTrackingNumber(trackingNumber);
        if (order == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Order not found");
        }

        // Retrieve SpecificGame instances associated with the order
        List<SpecificGame> specificGames = new ArrayList<>();
        for (SpecificGame specificGame : specificGameService.getAllSpecificGames()) {
            if (specificGame.getOrder().contains(order)) {
                specificGames.add(specificGame);
            }
        }
        return specificGames;
    }

    @Transactional
    public void returnGame(String trackingNumber, int specificGameId, String customerNote) {
        // Find and validate the order
        Order order = getOrderByTrackingNumber(trackingNumber);
        if (order == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Order not found");
        }

        // Check if the return is within the allowable period (7 days)
        Date currentDate = new Date();
        long differenceInMilliseconds = currentDate.getTime() - order.getOrderDate().getTime();
        long differenceInDays = differenceInMilliseconds / (1000 * 60 * 60 * 24);

        if (differenceInDays > 7) {
            throw new GameShopException(HttpStatus.BAD_REQUEST,
                    "Game return period has expired. Returns are allowed within 7 days of the order date.");
        }

        // Find and validate the specific game
        SpecificGame specificGame = specificGameService.findSpecificGameById(specificGameId);
        if (specificGame == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "SpecificGame not found");
        }

        // Verify that the game is part of the given order
        if (!getSpecificGamesByOrder(trackingNumber).contains(specificGame)) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game not in order");
        }
        if (SpecificGame.ItemStatus.Returned == specificGame.getItemStatus()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Game already returned");
        }

        // Update the specific game's item status to Returned
        specificGame.setItemStatus(SpecificGame.ItemStatus.Returned);
        specificGameService.updateSpecificGameItemStatus(specificGame.getSpecificGame_id(),
                SpecificGame.ItemStatus.Returned);

        // Get the associated game and increment the stock quantity
        Game game = specificGame.getGames();
        game.setStockQuantity(game.getStockQuantity() + 1);

        // Persist the stock quantity update
        gameService.updateGameStockQuantity(game.getGame_id(), game.getStockQuantity());

        // Update the order note if a customer note is provided
        if (customerNote != null && !customerNote.trim().isEmpty()) {
            order.setNote(customerNote.trim());
            orderRepository.save(order);
        }
    }

    @Transactional
    public List<Order> getOrdersWithCustomerEmail(String customerEmail) {
        // Validate customer email
        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Customer email cannot be empty or null");
        }

        // Retrieve customer by email
        Customer customer = customerRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Customer not found");
        }

        // Retrieve orders associated with the customer
        List<Order> orders = new ArrayList<>();
        for (Order order : getAllOrders()) {
            if (order.getCustomer().equals(customer)) {
                orders.add(order);
            }
        }
        if (orders.isEmpty()) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "No orders found for customer email: " + customerEmail);
        }

        return orders;
    }
}