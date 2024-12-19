package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Manager;

public interface ManagerRepository extends CrudRepository<Account, String> {
    public Manager findByEmail(String email);
}