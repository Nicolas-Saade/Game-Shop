package com.mcgill.ecse321.GameShop.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderAddGameRequestDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderListDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderRequestDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderResponseDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderSummaryDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameListDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameResponseDto;
import com.mcgill.ecse321.GameShop.dto.SpecificGameDto.SpecificGameSummaryDto;
import com.mcgill.ecse321.GameShop.model.Order;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.service.OrderService;

@RestController
@CrossOrigin(origins = "*")
public class OrderController {

        @Autowired
        private OrderService orderService;

        /**
         * Create a new order.
         * 
         * @param request the order request data transfer object containing the details
         *                of the order to be created
         * @return the response data transfer object containing the details of the
         *         created order
         */
        @PostMapping("/orders")
        public OrderResponseDto createOrder(@RequestBody OrderRequestDto request) {
                // Create the order
                Order order = orderService.createOrder(
                                request.getOrderDate(),
                                request.getNote(),
                                request.getPaymentCard(),
                                request.getCustomerEmail());

                // retrieve the specific games associated with the order
                List<SpecificGameResponseDto> specificGames = orderService
                                .getSpecificGamesByOrder(order.getTrackingNumber())
                                .stream()
                                .map(SpecificGameResponseDto::new)
                                .collect(Collectors.toList());

                return OrderResponseDto.create(order, specificGames);
        }

        /**
         * Get an order by tracking number.
         * 
         * @param trackingNumber the tracking number of the order to retrieve
         * @return the response data transfer object containing the details of the
         *         retrieved order
         */
        @GetMapping("/orders/{trackingNumber}")
        public OrderResponseDto getOrderByTrackingNumber(@PathVariable String trackingNumber) {
                // get the order by tracking number
                Order order = orderService.getOrderByTrackingNumber(trackingNumber);

                // retrieve the specific games associated with the order
                List<SpecificGameResponseDto> specificGames = orderService.getSpecificGamesByOrder(trackingNumber)
                                .stream()
                                .map(SpecificGameResponseDto::new)
                                .collect(Collectors.toList());
                return OrderResponseDto.create(order, specificGames);
        }

        /**
         * Get all orders.
         * 
         * @return a list data transfer object containing the summaries of all orders
         */
        @GetMapping("/orders")
        public OrderListDto getAllOrders() {
                List<OrderSummaryDto> dtos = orderService.getAllOrders().stream()
                                .map(OrderSummaryDto::new)
                                .collect(Collectors.toList());
                return new OrderListDto(dtos);
        }

        /**
         * Update an existing order.
         * 
         * @param trackingNumber the tracking number of the order to update
         * @param request        the order request data transfer object containing the
         *                       updated details of the order
         * @return the response data transfer object containing the details of the
         *         updated order
         */
        @PutMapping("/orders/{trackingNumber}")
        public OrderResponseDto updateOrder(
                        @PathVariable String trackingNumber,
                        @RequestBody OrderRequestDto request) {
                // Update the order
                Order order = orderService.updateOrder(
                                trackingNumber,
                                request.getOrderDate(),
                                request.getNote(),
                                request.getPaymentCard());

                // Retrieve the specific games associated with the order
                List<SpecificGameResponseDto> specificGames = orderService
                                .getSpecificGamesByOrder(order.getTrackingNumber())
                                .stream()
                                .map(SpecificGameResponseDto::new)
                                .collect(Collectors.toList());

                return OrderResponseDto.create(order, specificGames);
        }

        /**
         * Add a game to an order.
         * 
         * @param trackingNumber the tracking number of the order to add the game to
         * @param request        the order add game request data transfer object
         *                       containing the game ID and quantity to add
         * @return the response data transfer object containing the details of the
         *         updated order
         */
        @PutMapping("/orders/{trackingNumber}/games")
        public OrderResponseDto addGameToOrder(
                        @PathVariable String trackingNumber,
                        @RequestBody OrderAddGameRequestDto request) {
                // Add the game to the order
                orderService.addGameToOrder(trackingNumber, request.getGameId(), request.getQuantity());

                // Get the order by tracking number
                Order order = orderService.getOrderByTrackingNumber(trackingNumber);

                // Retrieve the specific games associated with the order
                List<SpecificGameResponseDto> specificGames = orderService.getSpecificGamesByOrder(trackingNumber)
                                .stream()
                                .map(SpecificGameResponseDto::new)
                                .collect(Collectors.toList());
                return OrderResponseDto.create(order, specificGames);

        }

        /**
         * Get specific games by order tracking number.
         * 
         * @param trackingNumber the tracking number of the order to retrieve specific
         *                       games from
         * @return a list data transfer object containing the summaries of the specific
         *         games in the order
         */
        @GetMapping("/orders/{trackingNumber}/specificGames")
        public SpecificGameListDto getSpecificGamesByOrder(@PathVariable String trackingNumber) {
                List<SpecificGame> specificGames = orderService.getSpecificGamesByOrder(trackingNumber);

                // Map SpecificGame to SpecificGameSummaryDto
                List<SpecificGameSummaryDto> gameSummaries = specificGames.stream()
                                .map(SpecificGameSummaryDto::new)
                                .collect(Collectors.toList());

                return new SpecificGameListDto(gameSummaries);
        }

        /**
         * Return a specific game from an order.
         * 
         * @param trackingNumber the tracking number of the order to return the game
         *                       from
         * @param specificGameId the ID of the specific game to return
         * @param note           an optional note provided by the customer for the
         *                       return
         * @return the response data transfer object containing the details of the
         *         updated order
         */
        @PutMapping("/orders/{trackingNumber}/return/{specificGameId}")
        public OrderResponseDto returnGame(
                        @PathVariable String trackingNumber,
                        @PathVariable int specificGameId,
                        @RequestBody(required = false) String note) {

                // Return the game with the optional note
                orderService.returnGame(trackingNumber, specificGameId, note);

                // Get the updated order by tracking number
                Order order = orderService.getOrderByTrackingNumber(trackingNumber);

                // Get the specific games associated with the order
                List<SpecificGameResponseDto> specificGames = orderService.getSpecificGamesByOrder(trackingNumber)
                                .stream()
                                .map(SpecificGameResponseDto::new)
                                .collect(Collectors.toList());

                return OrderResponseDto.create(order, specificGames);
        }

        /**
         * Get orders by customer email.
         * 
         * @param customerEmail the email of the customer to retrieve orders for
         * @return a list data transfer object containing the summaries of the orders
         *         for the specified customer
         */
        @GetMapping("/orders/customer/{customerEmail}")
        public OrderListDto getOrdersByCustomerEmail(@PathVariable String customerEmail) {
                // Get the orders associated with the customer
                List<Order> orders = orderService.getOrdersWithCustomerEmail(customerEmail);

                // Map Order to OrderSummaryDto
                List<OrderSummaryDto> orderDtos = orders.stream().map(OrderSummaryDto::new)
                                .collect(Collectors.toList());

                return new OrderListDto(orderDtos);
        }

}