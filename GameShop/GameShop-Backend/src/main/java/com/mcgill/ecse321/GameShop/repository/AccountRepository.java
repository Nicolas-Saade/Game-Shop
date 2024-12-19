package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Account;

public interface AccountRepository extends CrudRepository<Account, String> {
   public Account findByEmail(String email);
}