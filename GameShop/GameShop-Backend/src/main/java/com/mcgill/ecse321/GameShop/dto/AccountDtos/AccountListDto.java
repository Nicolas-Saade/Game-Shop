package com.mcgill.ecse321.GameShop.dto.AccountDtos;
import java.util.List;



public class AccountListDto {
   private List<AccountResponseDto> accounts;

   protected AccountListDto(){}

   public AccountListDto(List<AccountResponseDto> accounts){
        this.accounts = accounts;
   }

   public List<AccountResponseDto> getAccounts(){
        return accounts;
   }

   public void setAccounts(List<AccountResponseDto> accounts){
        this.accounts = accounts;
   }
}
