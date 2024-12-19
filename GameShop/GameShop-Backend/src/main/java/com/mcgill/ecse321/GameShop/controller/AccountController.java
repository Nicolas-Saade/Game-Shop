package com.mcgill.ecse321.GameShop.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountListDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountRequestDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountResponseDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountType;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.EmployeeListDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.EmployeeResponseDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.LoginRequestDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.LoginResponseDto;
import com.mcgill.ecse321.GameShop.dto.WishListDto.WishListResponseDto;
import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Employee;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.WishList;
import com.mcgill.ecse321.GameShop.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Creates a new customer account.
     * 
     * @param customerToCreate the account request data for the customer
     * @return the response DTO containing the created customer account information
     */
    @PostMapping("/account/customer")
    public AccountResponseDto createCustomer(@Valid @RequestBody AccountRequestDto customerToCreate) {
        // Create the customer account
        Customer createdCustomer = accountService.createCustomer(customerToCreate.getEmail(),
                customerToCreate.getUsername(),
                customerToCreate.getPassword(),
                customerToCreate.getPhoneNumber(),
                customerToCreate.getAddress());
        return AccountResponseDto.create(createdCustomer);
    }

    /**
     * Creates a new manager account.
     * 
     * @param managerToCreate the account request data for the manager
     * @return the response DTO containing the created manager account information
     */
    @PostMapping("/account/manager")
    public AccountResponseDto createManager(@Valid @RequestBody AccountRequestDto managerToCreate) {
        // Create the manager account
        Account createdManager = accountService.createManager(managerToCreate.getEmail(),
                managerToCreate.getUsername(),
                managerToCreate.getPassword(),
                managerToCreate.getPhoneNumber(),
                managerToCreate.getAddress());
        return AccountResponseDto.create(createdManager);
    }

    /**
     * Creates a new employee account.
     * 
     * @param employeeToCreate the account request data for the employee
     * @return the response DTO containing the created employee account information
     */
    @PostMapping("/account/employee")
    public EmployeeResponseDto createEmployee(@Valid @RequestBody AccountRequestDto employeeToCreate) {
        // Create the employee account
        Account createdEmployee = accountService.createEmployee(employeeToCreate.getEmail(),
                employeeToCreate.getUsername(),
                employeeToCreate.getPassword(),
                employeeToCreate.getPhoneNumber(),
                employeeToCreate.getAddress());
        return EmployeeResponseDto.create(createdEmployee);
    }

    /**
     * Finds a customer account by email.
     *
     * @param email the email of the customer
     * @return the account response DTO containing the customer account information
     */
    @GetMapping("/account/customer/{email}")
    public AccountResponseDto findCustomerByEmail(@PathVariable String email) {
        // Find the customer account
        Account createdAccount = accountService.getCustomerAccountByEmail(email);
        return AccountResponseDto.create(createdAccount);
    }

    /**
     * Finds an employee account by email.
     *
     * @param email the email of the employee
     * @return the employee response DTO containing the employee account information
     */
    @GetMapping("/account/employee/{email}")
    public EmployeeResponseDto findEmployeeByEmail(@PathVariable String email) {
        // Find the employee account
        Employee createdAccount = (Employee) accountService.getEmployeeAccountByEmail(email);
        return EmployeeResponseDto.create(createdAccount);
    }

    /**
     * Finds a manager account by email.
     *
     * @param email the email of the manager
     * @return the account response DTO containing the manager account information
     */
    @GetMapping("/account/getmanager")
    public AccountResponseDto getManager() {
        // Find the manager account
        Account manager = accountService.getManager();
        return AccountResponseDto.create(manager);
    }

    /**
     * Returns all the employees in the sytem.
     * 
     * @return a list of employees as a list of employee response DTOs
     */
    @GetMapping("/account/employees")
    public EmployeeListDto getEmployees() {
        List<EmployeeResponseDto> dtos = new ArrayList<EmployeeResponseDto>();

        // Loop through all employees and add them to the list
        for (Account e : accountService.getAllEmployees()) {
            if (e instanceof Employee) {
                dtos.add(new EmployeeResponseDto(e));
            } else {
                continue;
            }
        }
        return new EmployeeListDto(dtos);
    }

    /**
     * Returns all the customers in the system.
     * 
     * @return a list of customers returned as a list of account response DTOs
     */
    @GetMapping("/account/customers")
    public AccountListDto getCustomers() {
        List<AccountResponseDto> dtos = new ArrayList<AccountResponseDto>();

        // Loop through all customers and add them to the list
        for (Account c : accountService.getAllCustomers()) {
            if (c instanceof Customer) {
                dtos.add(new AccountResponseDto(c));
            } else {
                continue;
            }
        }
        return new AccountListDto(dtos);
    }

    /**
     * Updates an account with the provided information.
     * 
     * @param email              the email of the account to be updated
     * @param updatedInformation the updated information for the account
     * @return the account response DTO containing the updated account information
     */
    @PutMapping("account/{email}")
    public AccountResponseDto updateAccount(@PathVariable String email,
            @RequestBody AccountRequestDto updatedInformation) {
        // Get the account and update the information
        Account account = accountService.getAccountByEmail(email);
        accountService.updateAccount(email, updatedInformation.getUsername(),
                updatedInformation.getPassword(),
                updatedInformation.getPhoneNumber(),
                updatedInformation.getAddress());
        return AccountResponseDto.create(account);
    }

    /**
     * Archives a customer account by email.
     *
     * @param email the email of the customer
     * @return the account response DTO containing the customer account information
     */
    @PutMapping("account/employee/{email}")
    public EmployeeResponseDto archiveEmployeeAccount(@PathVariable String email) {
        // Call the service method to archive the employee account
        Account employee = (Account) accountService.archiveEmployee(email);
        return EmployeeResponseDto.create(employee);
    }

    /**
     * Returns the customer's wishlist given the customer's email.
     *
     * @param email the email of the customer
     * @return the wishlist response DTO containing the customer's wishlist
     */
    @GetMapping("/account/customer/{email}/wishlist")
    public WishListResponseDto getCustomerWishlist(@PathVariable String email) {
        // Get the customer's wishlist
        WishList wishlist = accountService.getWishlistByCustomerEmail(email);
        return new WishListResponseDto(wishlist);
    }

    @PostMapping("/account/login")
public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) {
    String email = loginRequest.getEmail();
    String password = loginRequest.getPassword();

    // Validate Customer
    Account account = accountService.getAccountByEmail(email);
    if (account != null && (account instanceof Customer) && account.getPassword().equals(password)) {
        return new LoginResponseDto(account.getEmail(), AccountType.CUSTOMER);
    }

    // Validate Employee
    if (account != null && (account instanceof Employee) && account.getPassword().equals(password)) {
        return new LoginResponseDto(account.getEmail(), AccountType.EMPLOYEE);
    }

    // Validate Manager
    if (account != null && (account instanceof Manager) && account.getEmail().equals(email) && account.getPassword().equals(password)) {
        return new LoginResponseDto(account.getEmail(), AccountType.MANAGER);
    }

    // If no account matches
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
}



}