package com.mcgill.ecse321.GameShop.dto.CartDto;

import com.mcgill.ecse321.GameShop.model.Cart;

public class CartSummaryDto {
    private int cartId;
    private int totalItems;
    private double totalPrice;

    public CartSummaryDto() {
    }

    public CartSummaryDto(Cart cart, int totalItems, double totalPrice2) {
        this.cartId = cart.getCart_id();
        this.totalItems = totalItems;
        this.totalPrice = totalPrice2;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
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

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}