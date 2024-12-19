package com.mcgill.ecse321.GameShop.dto.SpecificGameDto;

import java.util.ArrayList;
import java.util.List;

import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderListDto;
import com.mcgill.ecse321.GameShop.dto.OrderDto.OrderSummaryDto;
import com.mcgill.ecse321.GameShop.model.Order;
import com.mcgill.ecse321.GameShop.model.SpecificGame;
import com.mcgill.ecse321.GameShop.model.SpecificGame.ItemStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SpecificGameResponseDto {
    
        @Positive(message = "Specific game ID cannot be negative")
        private int specificGame_id;
        @NotNull(message = "Specific game item status cannot be null")
        private ItemStatus itemStatus;

        @NotBlank(message = "Specific game linked orders cannot be empty")
        private OrderListDto orders;
        @Positive(message = "Specific game ID cannot be negative")
        private int game_id;

        public SpecificGameResponseDto(SpecificGame specificGame) {
            this.specificGame_id = specificGame.getSpecificGame_id();
            this.itemStatus = specificGame.getItemStatus();
            this.game_id = specificGame.getGames().getGame_id();

            List<Order> orderList = specificGame.getOrder();
            List<OrderSummaryDto> orders = new ArrayList<OrderSummaryDto>();
            for (Order order : orderList) { 
                orders.add(new OrderSummaryDto(order));
            }
            OrderListDto listOfOrders = new OrderListDto(orders);

            this.orders = listOfOrders;
        }

        public static SpecificGameResponseDto create(SpecificGame specificGame) {
            return new SpecificGameResponseDto(specificGame);
        }
        protected SpecificGameResponseDto() {
        }

        public int getSpecificGame_id() {
            return specificGame_id;
        }

        public ItemStatus getItemStatus() {
            return itemStatus;
        }

        public OrderListDto getOrders() {
            return orders;
        }

        public int getGame_id() {
            return game_id;
        }
}
