package com.mcgill.ecse321.GameShop.dto.AccountDtos;
public class LoginResponseDto {
    private String email;
    private AccountType accountType;
    public LoginResponseDto(String email, AccountType accountType) {
        this.email = email;
        this.accountType = accountType;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public AccountType getType() {
        return accountType;
    }
    public void setType(AccountType type) {
        this.accountType = type;
    }
    
}