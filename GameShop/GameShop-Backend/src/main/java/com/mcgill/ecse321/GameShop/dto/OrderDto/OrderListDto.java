package com.mcgill.ecse321.GameShop.dto.OrderDto;

import java.util.List;

public class OrderListDto {
    private List<OrderSummaryDto> orders;

    public OrderListDto(List<OrderSummaryDto> orders) {
        this.orders = orders;
    }
    protected OrderListDto() {
    }

    public List<OrderSummaryDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderSummaryDto> orders) {
        this.orders = orders;
    }
}
