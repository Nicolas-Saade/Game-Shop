package com.mcgill.ecse321.GameShop.dto.OrderDto;

import com.mcgill.ecse321.GameShop.model.Order;

import java.util.Date;

public class OrderSummaryDto {
    private String trackingNumber;
    private Date orderDate;
    private String customerEmail;

    public OrderSummaryDto(Order order) {
        this.trackingNumber = order.getTrackingNumber();
        this.orderDate = order.getOrderDate();
        this.customerEmail = order.getCustomer().getEmail();
    }
    protected OrderSummaryDto() {
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
}
