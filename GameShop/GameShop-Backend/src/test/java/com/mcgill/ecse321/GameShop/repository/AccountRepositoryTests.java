package com.mcgill.ecse321.GameShop.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Customer;
import com.mcgill.ecse321.GameShop.model.Employee;
import com.mcgill.ecse321.GameShop.model.Manager;

import jakarta.transaction.Transactional;

@SpringBootTest
public class AccountRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CartRepository cartRepository;

    // Clear the database before and after each test
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        accountRepository.deleteAll();
        cartRepository.deleteAll();
        Account.clearTestEmails(AccountRepositoryTests.testEmails);
        AccountRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadCustomerAccount() {
        // Create Customer 1
        String email = "1@1.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9294";
        String address = "1234 rue Sainte-Catherine";

        // Create and save a new cart
        Cart cart = new Cart();
        cart = cartRepository.save(cart);
        int cartId = cart.getCart_id();

        // Create and save a new customer
        Customer customer1 = new Customer(email, username, password, phoneNumber, address, cart);
        AccountRepositoryTests.testEmails.add(customer1.getEmail());
        customer1 = accountRepository.save(customer1);

        // Retrieve the customer by email
        Account account2 = accountRepository.findByEmail(email);

        // Assertions to verify the customer was saved and retrieved correctly
        assertNotNull(customer1);
        assertEquals(email, account2.getEmail());
        assertEquals(username, account2.getUsername());
        assertEquals(password, account2.getPassword());
        assertEquals(phoneNumber, account2.getPhoneNumber());
        assertEquals(address, account2.getAddress());
        assertTrue(account2 instanceof Customer, "The account should be a customer.");
        assertEquals(cartId, ((Customer) account2).getCart().getCart_id());
    }

    @Test
    @Transactional
    public void testCreateAndReadCustomerWithCartReplacement() {
        // Create first customer
        String email = "1@1.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9294";
        String address = "1234 rue Sainte-Catherine";

        // Create and save the first cart
        Cart firstCart = new Cart();
        firstCart = cartRepository.save(firstCart);
        int firstCardId = firstCart.getCart_id();

        // Create and save the first customer
        Customer firstCustomer = new Customer(email, username, password, phoneNumber, address, firstCart);
        AccountRepositoryTests.testEmails.add(firstCustomer.getEmail());
        firstCustomer = accountRepository.save(firstCustomer);

        // Retrieve the first customer by email
        Account pulledFirstCustomer = accountRepository.findByEmail(email);

        // Create and save the second cart
        Cart secondCart = new Cart();
        secondCart = cartRepository.save(secondCart);
        int secondCartId = secondCart.getCart_id();

        // Replace the cart of the first customer
        firstCustomer.setCart(secondCart);

        // Assertions to verify the cart replacement
        assertNotNull(firstCustomer);
        assertEquals(email, pulledFirstCustomer.getEmail());
        assertEquals(username, pulledFirstCustomer.getUsername());
        assertEquals(password, pulledFirstCustomer.getPassword());
        assertEquals(phoneNumber, pulledFirstCustomer.getPhoneNumber());
        assertEquals(address, pulledFirstCustomer.getAddress());
        assertTrue(pulledFirstCustomer instanceof Customer, "The account should be a customer.");
        assertNotEquals(firstCardId, ((Customer) pulledFirstCustomer).getCart().getCart_id());
        assertEquals(secondCartId, ((Customer) pulledFirstCustomer).getCart().getCart_id());
    }

    @Test
    @Transactional
    public void testCreateAndReadEmployeeAccount() {
        // Create first employee
        String email = "1@1.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9295";
        String address = "1234 rue Sainte-Catherine";

        // Create and save the first employee
        Employee createdFirstEmployee = new Employee(email, username, password, phoneNumber, address);
        AccountRepositoryTests.testEmails.add(createdFirstEmployee.getEmail());
        createdFirstEmployee = accountRepository.save(createdFirstEmployee);

        // Retrieve the first employee by email
        Account pulledFirstEmployee = accountRepository.findByEmail(email);

        // Create second employee
        String email2 = "10@10.com";
        String username2 = "AnthonySaber";
        String password2 = "password";
        String phoneNumber2 = "+1 (438) 865-9295";
        String address2 = "1234 rue Sainte-Catherine";

        // Create and save the second employee
        Employee createdSecondEmployee = new Employee(email2, username2, password2, phoneNumber2, address2);
        AccountRepositoryTests.testEmails.add(createdSecondEmployee.getEmail());

        createdSecondEmployee = accountRepository.save(createdSecondEmployee);

        // Retrieve the second employee by email
        Account pulledSecondEmployee = accountRepository.findByEmail(email2);

        // Assertions to verify both employees were saved and retrieved correctly
        assertNotNull(createdFirstEmployee);
        assertEquals(email, pulledFirstEmployee.getEmail());
        assertEquals(username, pulledFirstEmployee.getUsername());
        assertEquals(password, pulledFirstEmployee.getPassword());
        assertEquals(phoneNumber, pulledFirstEmployee.getPhoneNumber());
        assertEquals(address, pulledFirstEmployee.getAddress());
        assertTrue(pulledFirstEmployee instanceof Employee, "The account should be an employee.");

        assertNotNull(createdSecondEmployee);
        assertEquals(email2, pulledSecondEmployee.getEmail());
        assertEquals(username2, pulledSecondEmployee.getUsername());
        assertEquals(password2, pulledSecondEmployee.getPassword());
        assertEquals(phoneNumber2, pulledSecondEmployee.getPhoneNumber());
        assertEquals(address2, pulledSecondEmployee.getAddress());
        assertTrue(pulledSecondEmployee instanceof Employee, "The account should be an employee.");

        // Verify the total number of employees in the repository
        List<Account> employees = (List<Account>) accountRepository.findAll();
        assertEquals(2, employees.size());
    }

    @Test
    @Transactional
    public void testCreateAndReadManagerAccount() {
        // Create Manager
        String email = "1@1.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9293";
        String address = "1234 rue Sainte-Catherine";

        // Create and save the manager
        Manager createdManager = new Manager(email, username, password, phoneNumber, address);
        AccountRepositoryTests.testEmails.add(createdManager.getEmail());
        createdManager = accountRepository.save(createdManager);

        // Retrieve the manager by email
        Account pulledManager = accountRepository.findByEmail(email);

        // Assertions to verify the manager was saved and retrieved correctly
        assertNotNull(createdManager);
        assertEquals(email, pulledManager.getEmail());
        assertEquals(username, pulledManager.getUsername());
        assertEquals(password, pulledManager.getPassword());
        assertEquals(phoneNumber, pulledManager.getPhoneNumber());
        assertEquals(address, pulledManager.getAddress());
        assertTrue(pulledManager instanceof Manager, "The account should be a manager.");

        // Verify the total number of managers in the repository
        List<Account> managers = (List<Account>) accountRepository.findAll();
        assertNotNull(managers);
        assertEquals(1, managers.size());
    }
}
