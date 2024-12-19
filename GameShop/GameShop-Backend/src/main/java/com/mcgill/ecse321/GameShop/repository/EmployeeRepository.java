package com.mcgill.ecse321.GameShop.repository;

import org.springframework.data.repository.CrudRepository;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Employee;

public interface EmployeeRepository extends CrudRepository<Account, String> {
    public Employee findByEmail(String email);
}