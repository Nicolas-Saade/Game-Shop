package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Customer;

public interface CustomerRepository extends CrudRepository<Account, String> {
   public Customer findByEmail(String email);
}