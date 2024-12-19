package com.mcgill.ecse321.GameShop.dto.WishListDto;


import jakarta.validation.constraints.NotBlank;

public class WishListRequestDto {
    @NotBlank(message ="WishList name cannot be empty")
    private String title;
    @NotBlank(message = "Email cannot be empty")
    private String customerEmail;

    protected WishListRequestDto() {
    }
    public WishListRequestDto(String title, String customerEmail) {
        this.title = title;
        this.customerEmail = customerEmail;
    }
    public String getTitle() {
        return title;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
 
}
