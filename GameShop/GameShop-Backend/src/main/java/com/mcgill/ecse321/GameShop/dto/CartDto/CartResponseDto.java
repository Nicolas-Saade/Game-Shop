package com.mcgill.ecse321.GameShop.dto.CartDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mcgill.ecse321.GameShop.model.Cart;

public class CartResponseDto {
    private int cartId;
    private List<CartGameDto> games;
    private int totalItems;
    private double totalPrice;

    public CartResponseDto() {
    }

    public CartResponseDto(int cartId, List<CartGameDto> games, int totalItems, double totalPrice2) {
        this.cartId = cartId;
        this.games = games;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice2;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public List<CartGameDto> getGames() {
        return games;
    }

    public void setGames(List<CartGameDto> games) {
        this.games = games;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = Math.round(totalPrice * 100.0) / 100.0;
    }

    public static CartResponseDto create(Cart cart, Map<Integer, Integer> quantities) {
        List<CartGameDto> games = cart.getGames().stream()
                .map(game -> new CartGameDto(game, quantities.getOrDefault(game.getGame_id(), 1)))
                .collect(Collectors.toList());

        int totalItems = quantities.values().stream().mapToInt(Integer::intValue).sum();
        double totalPrice = cart.getGames().stream()
                .mapToDouble(game -> game.getPrice() * quantities.getOrDefault(game.getGame_id(), 1))
                .sum();

        return new CartResponseDto(cart.getCart_id(), games, totalItems, totalPrice);
    }
}