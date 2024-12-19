package com.mcgill.ecse321.GameShop.repository;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Manager;

import jakarta.transaction.Transactional;

@SpringBootTest
public class ManagerRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private CartRepository cartRepository;

    // Clear the database before and after each test to ensure a clean state
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        managerRepository.deleteAll();
        cartRepository.deleteAll();
        Account.clearTestEmails(ManagerRepositoryTests.testEmails);
        ManagerRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadManagerAccountAsAccount() {
        // Create Manager
        String email = "mohamed@hotmail.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9293";
        String address = "1234 rue Sainte-Catherine";

        // Save the created manager to the repository
        Manager createdManager = new Manager(email, username, password, phoneNumber, address);
        createdManager = managerRepository.save(createdManager);
        ManagerRepositoryTests.testEmails.add(createdManager.getEmail());

        // Retrieve the manager by email
        Account pulledManager = managerRepository.findByEmail(email);

        // Assertions to verify the manager was correctly saved and retrieved
        assertNotNull(createdManager);
        assertEquals(email, pulledManager.getEmail());
        assertEquals(username, pulledManager.getUsername());
        assertEquals(password, pulledManager.getPassword());
        assertEquals(phoneNumber, pulledManager.getPhoneNumber());
        assertEquals(address, pulledManager.getAddress());
        assertTrue(pulledManager instanceof Manager, "The account should be a manager.");

        // Retrieve all managers and verify the count
        List<Account> managers = (List<Account>) managerRepository.findAll();
        assertNotNull(managers);
        assertEquals(1, managers.size());
    }
}
