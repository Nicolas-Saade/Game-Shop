package com.mcgill.ecse321.GameShop.dto.CartDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartRequestDto {
    @NotNull(message = "Game ID is required.")
    @Min(value = 1, message = "Game ID must be positive.")
    private Integer gameId;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;

    public CartRequestDto() {
    }

    public CartRequestDto(Integer gameId, Integer quantity) {
        this.gameId = gameId;
        this.quantity = quantity;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}