package com.mcgill.ecse321.GameShop.dto.CartDto;

import java.util.List;

public class CartListDto {
    private List<CartSummaryDto> carts;

    public CartListDto() {
    }

    public CartListDto(List<CartSummaryDto> carts) {
        this.carts = carts;
    }

    public List<CartSummaryDto> getCarts() {
        return carts;
    }

    public void setCarts(List<CartSummaryDto> carts) {
        this.carts = carts;
    }
}