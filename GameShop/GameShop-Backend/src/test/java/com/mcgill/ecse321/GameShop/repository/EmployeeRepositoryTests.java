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

import jakarta.transaction.Transactional;

@SpringBootTest
public class EmployeeRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CartRepository cartRepository;

    // Clear the database before and after each test to ensure a clean state
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        employeeRepository.deleteAll();
        cartRepository.deleteAll();
        Account.clearTestEmails(EmployeeRepositoryTests.testEmails);
        EmployeeRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadEmployeeAccountAsAccount() {
        // Create and save the first employee
        String email = "mohamed.abdelhady@mohamed.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9295";
        String address = "1234 rue Sainte-Catherine";

        Employee createdFirstEmployee = new Employee(email, username, password, phoneNumber, address);
        createdFirstEmployee = employeeRepository.save(createdFirstEmployee);
        EmployeeRepositoryTests.testEmails.add(createdFirstEmployee.getEmail());

        // Retrieve the first employee by email
        Account pulledFirstEmployee = employeeRepository.findByEmail(email);

        // Create and save the second employee
        String email2 = "22@22.com";
        String username2 = "AnthonySaber";
        String password2 = "password";
        String phoneNumber2 = "+1 (438) 865-9295";
        String address2 = "1234 rue Sainte-Catherine";

        Employee createdSecondEmployee = new Employee(email2, username2, password2, phoneNumber2, address2);
        createdSecondEmployee.setEmployeeStatus(Employee.EmployeeStatus.Archived);
        createdSecondEmployee = employeeRepository.save(createdSecondEmployee);
        EmployeeRepositoryTests.testEmails.add(createdSecondEmployee.getEmail());

        // Retrieve the second employee by email
        Account pulledSecondEmployee = employeeRepository.findByEmail(email2);

        // Assertions to verify the first employee's details
        assertNotNull(createdFirstEmployee);
        assertEquals(email, pulledFirstEmployee.getEmail());
        assertEquals(username, pulledFirstEmployee.getUsername());
        assertEquals(password, pulledFirstEmployee.getPassword());
        assertEquals(phoneNumber, pulledFirstEmployee.getPhoneNumber());
        assertEquals(address, pulledFirstEmployee.getAddress());
        assertTrue(pulledFirstEmployee instanceof Employee, "The account should be an employee.");
        assertEquals(createdFirstEmployee.getEmployeeStatus(), ((Employee) pulledFirstEmployee).getEmployeeStatus());

        // Assertions to verify the second employee's details
        assertNotNull(createdSecondEmployee);
        assertEquals(email2, pulledSecondEmployee.getEmail());
        assertEquals(username2, pulledSecondEmployee.getUsername());
        assertEquals(password2, pulledSecondEmployee.getPassword());
        assertEquals(phoneNumber2, pulledSecondEmployee.getPhoneNumber());
        assertEquals(address2, pulledSecondEmployee.getAddress());
        assertTrue(pulledSecondEmployee instanceof Employee, "The account should be an employee.");
        assertEquals(createdSecondEmployee.getEmployeeStatus(), ((Employee) pulledSecondEmployee).getEmployeeStatus());

        // Verify that there are exactly two employees in the repository
        List<Account> employees = (List<Account>) employeeRepository.findAll();
        assertEquals(2, employees.size());
    }
}
