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
import com.mcgill.ecse321.GameShop.model.Employee;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Staff;

import jakarta.transaction.Transactional;

@SpringBootTest
public class StaffRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CartRepository cartRepository;

    // Clear the database before and after each test
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        staffRepository.deleteAll();
        cartRepository.deleteAll();
        Account.clearTestEmails(StaffRepositoryTests.testEmails);
        StaffRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadEmployeeAccountAsStaff() {
        // Create first employee
        String email = "mohamed.abdelhady1@gmail.com";
        String username = "mohamed1";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9295";
        String address = "1234 rue Sainte-Catherine";

        Employee createdFirstEmployee = new Employee(email, username, password, phoneNumber, address);
        createdFirstEmployee = staffRepository.save(createdFirstEmployee);
        StaffRepositoryTests.testEmails.add(createdFirstEmployee.getEmail());

        // Retrieve the first employee by email
        Staff pulledFirstEmployee = staffRepository.findByEmail(email);

        // Create second employee
        String email2 = "mohamed.abdelhady2@gmail.com";
        String username2 = "mohamed2";
        String password2 = "password";
        String phoneNumber2 = "+1 (438) 865-9295";
        String address2 = "123 rue Peel";

        Employee createdSecondEmployee = new Employee(email2, username2, password2, phoneNumber2, address2);
        createdSecondEmployee = staffRepository.save(createdSecondEmployee);
        StaffRepositoryTests.testEmails.add(createdFirstEmployee.getEmail());

        // Retrieve the second employee by email
        Staff pulledSecondEmployee = staffRepository.findByEmail(email2);

        // Assertions for the first employee
        assertNotNull(createdFirstEmployee);
        assertEquals(email, pulledFirstEmployee.getEmail());
        assertEquals(username, pulledFirstEmployee.getUsername());
        assertEquals(password, pulledFirstEmployee.getPassword());
        assertEquals(phoneNumber, pulledFirstEmployee.getPhoneNumber());
        assertEquals(address, pulledFirstEmployee.getAddress());
        assertTrue(pulledFirstEmployee instanceof Employee, "The account should be an employee.");

        // Assertions for the second employee
        assertNotNull(createdSecondEmployee);
        assertEquals(email2, pulledSecondEmployee.getEmail());
        assertEquals(username2, pulledSecondEmployee.getUsername());
        assertEquals(password2, pulledSecondEmployee.getPassword());
        assertEquals(phoneNumber2, pulledSecondEmployee.getPhoneNumber());
        assertEquals(address2, pulledSecondEmployee.getAddress());
        assertTrue(pulledSecondEmployee instanceof Employee, "The account should be an employee.");

        // Verify the total number of employees
        List<Account> employees = (List<Account>) staffRepository.findAll();
        assertEquals(2, employees.size());
    }

    @Test
    @Transactional
    public void testCreateAndReadManagerAccountAsStaff() {
        // Create manager
        String email = "mohamed.abdelhady3@gmail.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9293";
        String address = "1234 rue Sainte-Catherine";

        Manager createdManager = new Manager(email, username, password, phoneNumber, address);
        createdManager = staffRepository.save(createdManager);

        // Retrieve the manager by email
        Staff pulledManager = staffRepository.findByEmail(email);

        // Assertions for the manager
        assertNotNull(createdManager);
        assertEquals(email, pulledManager.getEmail());
        assertEquals(username, pulledManager.getUsername());
        assertEquals(password, pulledManager.getPassword());
        assertEquals(phoneNumber, pulledManager.getPhoneNumber());
        assertEquals(address, pulledManager.getAddress());
        assertTrue(pulledManager instanceof Manager, "The account should be a manager.");

        // Verify the total number of managers
        List<Account> managers = (List<Account>) staffRepository.findAll();
        assertNotNull(managers);
        assertEquals(1, managers.size());
    }
}
