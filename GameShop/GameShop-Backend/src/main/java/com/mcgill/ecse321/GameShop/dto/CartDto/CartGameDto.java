package com.mcgill.ecse321.GameShop.dto.CartDto;

import com.mcgill.ecse321.GameShop.dto.GameDto.GameResponseDto;
import com.mcgill.ecse321.GameShop.model.Game;

public class CartGameDto {
    private GameResponseDto game;
    private int quantity;

    public CartGameDto() {
    }

    public CartGameDto(Game game, int quantity) {
        this.game = GameResponseDto.create(game);
        this.quantity = quantity;
    }

    public GameResponseDto getGame() {
        return game;
    }

    public void setGame(GameResponseDto game) {
        this.game = game;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
