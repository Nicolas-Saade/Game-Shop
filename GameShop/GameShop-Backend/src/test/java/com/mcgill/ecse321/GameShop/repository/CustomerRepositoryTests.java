package com.mcgill.ecse321.GameShop.repository;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Cart;
import com.mcgill.ecse321.GameShop.model.Customer;

import jakarta.transaction.Transactional;

@SpringBootTest
public class CustomerRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        // Clear the database before and after each test to ensure a clean state
        customerRepository.deleteAll();
        cartRepository.deleteAll();
        Account.clearTestEmails(CustomerRepositoryTests.testEmails);
        CustomerRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadCustomerAccountAsAccount() {
        // Create and save the first customer
        String email = "6@6.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9294";
        String address = "1234 rue Sainte-Catherine";

        Cart firstCart = new Cart();
        firstCart = cartRepository.save(firstCart);
        int firstCardId = firstCart.getCart_id();

        Customer firstCustomer = new Customer(email, username, password, phoneNumber, address, firstCart);
        firstCustomer = customerRepository.save(firstCustomer);
        CustomerRepositoryTests.testEmails.add(firstCustomer.getEmail());

        // Retrieve the first customer by email
        Account pulledFirstCustomer = customerRepository.findByEmail(email);

        // Create and save the second customer
        String email2 = "7@7.com";
        String username2 = "AnthonySaber2";
        String password2 = "password2";
        String phoneNumber2 = "+1 (438) 86522-9294";
        String address2 = "1234 rue Sainte-Cath22erine";

        Cart secondCart = new Cart();
        secondCart = cartRepository.save(secondCart);
        int secondCartId = secondCart.getCart_id();

        Customer secondCustomer = new Customer(email2, username2, password2, phoneNumber2, address2, secondCart);
        secondCustomer = customerRepository.save(secondCustomer);
        CustomerRepositoryTests.testEmails.add(firstCustomer.getEmail());

        // Retrieve the second customer by email
        Account pulledSecondCustomer = customerRepository.findByEmail(email2);

        // Assertions for the first customer
        assertNotNull(firstCustomer);
        assertEquals(email, pulledFirstCustomer.getEmail());
        assertEquals(username, pulledFirstCustomer.getUsername());
        assertEquals(password, pulledFirstCustomer.getPassword());
        assertEquals(phoneNumber, pulledFirstCustomer.getPhoneNumber());
        assertEquals(address, pulledFirstCustomer.getAddress());
        assertTrue(pulledFirstCustomer instanceof Customer, "The account should be a customer.");
        assertEquals(firstCardId, ((Customer) pulledFirstCustomer).getCart().getCart_id());

        // Assertions for the second customer
        assertNotNull(secondCustomer);
        assertEquals(email2, pulledSecondCustomer.getEmail());
        assertEquals(username2, pulledSecondCustomer.getUsername());
        assertEquals(password2, pulledSecondCustomer.getPassword());
        assertEquals(phoneNumber2, pulledSecondCustomer.getPhoneNumber());
        assertEquals(address2, pulledSecondCustomer.getAddress());
        assertTrue(pulledSecondCustomer instanceof Customer, "The account should be a customer.");
        assertEquals(secondCartId, ((Customer) pulledSecondCustomer).getCart().getCart_id());

        // Verify that both customers are saved in the repository
        List<Account> customers = (List<Account>) customerRepository.findAll();
        assertEquals(2, customers.size());
    }

    @Test
    @Transactional
    public void testCreateAndReadCustomerAsAccountWithCartReplacement() {
        // Create and save the first customer
        String email = "mohamed@mohamed.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9294";
        String address = "1234 rue Sainte-Catherine";

        Cart firstCart = new Cart();
        firstCart = cartRepository.save(firstCart);
        int firstCardId = firstCart.getCart_id();

        Customer firstCustomer = new Customer(email, username, password, phoneNumber, address, firstCart);
        firstCustomer = customerRepository.save(firstCustomer);
        CustomerRepositoryTests.testEmails.add(firstCustomer.getEmail());

        // Retrieve the first customer by email
        Account pulledFirstCustomer = customerRepository.findByEmail(email);

        // Create and save a new cart, then replace the customer's cart
        Cart secondCart = new Cart();
        secondCart = cartRepository.save(secondCart);
        int secondCartId = secondCart.getCart_id();

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
}
