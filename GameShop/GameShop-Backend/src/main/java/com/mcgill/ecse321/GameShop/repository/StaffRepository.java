package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Staff;

public interface StaffRepository extends CrudRepository<Account, String> {
    public Staff findByEmail(String email);
}