package com.mcgill.ecse321.GameShop.dto.OrderDto;

import jakarta.validation.constraints.*;

public class OrderAddGameRequestDto {
    @NotNull(message = "Game ID cannot be null")
    private int gameId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    // Constructors
    public OrderAddGameRequestDto() {
    }

    public OrderAddGameRequestDto(int gameId, int quantity) {
        this.gameId = gameId;
        this.quantity = quantity;
    }

    public static OrderAddGameRequestDto create(int gameId, int quantity) {
        return new OrderAddGameRequestDto(gameId, quantity);
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}