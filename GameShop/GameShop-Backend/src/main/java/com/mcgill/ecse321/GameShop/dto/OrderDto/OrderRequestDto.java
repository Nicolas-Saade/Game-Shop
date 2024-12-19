package com.mcgill.ecse321.GameShop.dto.OrderDto;

import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

public class OrderRequestDto {
    @NotNull(message = "Order date cannot be null")
    @PastOrPresent(message = "Order date must be in the past or present")
    private Date orderDate;

    @NotEmpty(message = "Note cannot be empty")
    @Size(max = 255, message = "Note cannot be longer than 255 characters")
    private String note;

    @NotNull(message = "Payment card cannot be null")
    @Size(min = 16, max = 16, message = "Card should be 16 digist long")
    private String paymentCard;

    @NotEmpty(message = "Customer email cannot be empty")
    private String customerEmail;

    // New Field
    @NotNull(message = "Specific game IDs cannot be null")
    @Size(min = 1, message = "At least one specific game ID must be provided")
    private List<Integer> specificGameIds;

    // Constructors

    public OrderRequestDto(Date orderDate, String note, String paymentCard, String customerEmail,
            List<Integer> specificGameIds) {
        this.orderDate = orderDate;
        this.note = note;
        this.paymentCard = paymentCard;
        this.customerEmail = customerEmail;
        this.specificGameIds = specificGameIds;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getNote() {
        return note;
    }

    protected OrderRequestDto() {
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(String paymentCard) {
        this.paymentCard = paymentCard;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<Integer> getSpecificGameIds() {
        return specificGameIds;
    }

    public void setSpecificGameIds(List<Integer> specificGameIds) {
        this.specificGameIds = specificGameIds;
    }
}