package com.mcgill.ecse321.GameShop.dto.AccountDtos;

import java.util.List;

public class EmployeeListDto {
    private List<EmployeeResponseDto> accounts;
    
    protected EmployeeListDto(){}
   public EmployeeListDto(List<EmployeeResponseDto> accounts){
        this.accounts = accounts;
   }

   public List<EmployeeResponseDto> getAccounts(){
        return accounts;
   }

   public void setAccounts(List<EmployeeResponseDto> accounts){
        this.accounts = accounts;
   }
}
