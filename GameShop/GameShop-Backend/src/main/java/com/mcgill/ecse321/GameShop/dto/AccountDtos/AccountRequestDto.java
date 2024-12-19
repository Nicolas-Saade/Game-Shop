package com.mcgill.ecse321.GameShop.dto.AccountDtos;

import jakarta.validation.constraints.NotBlank;

public class AccountRequestDto {
	@NotBlank(message = "Email is required.")
    private String email;
    @NotBlank(message = "Username is required.")
    private String username;
    @NotBlank(message = "Password is required.")
    private String password;
    private String phoneNumber;
    private String address;
    private AccountType accountType;
    
  
    public AccountRequestDto(String email, String username, String password, String phoneNumber, String address) {
        this.accountType = AccountType.MANAGER;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType type) {
        this.accountType = type;
    }


}
