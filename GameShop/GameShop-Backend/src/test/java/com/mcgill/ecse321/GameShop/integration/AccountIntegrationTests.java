package com.mcgill.ecse321.GameShop.integration;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountListDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountRequestDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountResponseDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.AccountType;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.EmployeeListDto;
import com.mcgill.ecse321.GameShop.dto.AccountDtos.EmployeeResponseDto;
import com.mcgill.ecse321.GameShop.dto.WishListDto.WishListResponseDto;
import com.mcgill.ecse321.GameShop.model.Employee.EmployeeStatus;
import com.mcgill.ecse321.GameShop.repository.AccountRepository;
import com.mcgill.ecse321.GameShop.repository.CartRepository;
import com.mcgill.ecse321.GameShop.repository.CustomerRepository;
import com.mcgill.ecse321.GameShop.repository.EmployeeRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.WishListRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class AccountIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private String customerEmail;
    private String managerEmail;
    private String employeeEmail;
    private String secondEmployeeEmail;

    private static final String MANAGER_EMAIL = "john@gmail.com";
    private static final String MANAGER_USERNAME = "john123";
    private static final String MANAGER_PASSWORD = "john909";
    private static final String MANAGER_PHONENUM = "438999000";
    private static final String MANAGER_ADDRESS = "900 rue sherbrooke";

    private static final String CUSTOMER_EMAIL = "annie@gmail.com";
    private static final String CUSTOMER_USERNAME = "a901k";
    private static final String CUSTOMER_PASSWORD = "annie123";
    private static final String CUSTOMER_PHONENUM = "4381111111";
    private static final String CUSTOMER_ADDRESS = "street 1";

    private static final String SECCUSTOMER_EMAIL = "lucas12@gmail.com";
    private static final String SECCUSTOMER_USERNAME = "lucasS";
    private static final String SECCUSTOMER_PASSWORD = "lucase";
    private static final String SECCUSTOMER_PHONENUM = "514091283";
    private static final String SECCUSTOMER_ADDRESS = "street c";

    private static final String EMPLOYEE_EMAIL = "jude123@gmail.com";
    private static final String EMPLOYEE_USERNAME = "judeSousou";
    private static final String EMPLOYEE_PASSWORD = "j123";
    private static final String EMPLOYEE_PHONENUM = "4380000000";
    private static final String EMPLOYEE_ADDRESS = "street a";

    private static final String SECEMPLOYEE_EMAIL = "jason@gmail.com";
    private static final String SECEMPLOYEE_USERNAME = "jason1029";
    private static final String SECEMPLOYEE_PASSWORD = "testing";
    private static final String SECEMPLOYEE_PHONENUM = "514091823";
    private static final String SECEMPLOYEE_ADDRESS = "street d";

    private static final String UPDATE_EMPLOYEE_USERNAME = "jude10111111";
    private static final String UPDATE_EMPLOYEE_PASSWORD = "alkdalkdal";
    private static final String UPDATE_EMPLOYEE_PHONENUM = "43820231000";
    private static final String UPDATE_EMPLOYEE_ADDRESS = "street 1012";

    @AfterAll
    public void clearDatabase() {
        wishListRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
        managerRepository.deleteAll();
        accountRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void testCreateValidManager() {
        // Arrange
        AccountRequestDto manager = new AccountRequestDto(MANAGER_EMAIL, MANAGER_USERNAME, MANAGER_PASSWORD,
                MANAGER_PHONENUM, MANAGER_ADDRESS);

        // Act
        ResponseEntity<AccountResponseDto> response = client.postForEntity("/account/manager", manager,
                AccountResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AccountResponseDto fetchedManager = response.getBody();
        assertNotNull(fetchedManager);
        AccountResponseDto manager1 = response.getBody();
        assertNotNull(manager1);
        assertEquals(MANAGER_EMAIL, manager1.getEmail());
        this.managerEmail = manager1.getEmail();
        assertEquals(MANAGER_USERNAME, manager1.getUsername());
        assertEquals(MANAGER_PHONENUM, manager1.getPhoneNumber());
        assertEquals(MANAGER_ADDRESS, manager1.getAddress());
        assertEquals(AccountType.MANAGER, manager1.getAccountType());
    }

    @Test
    @Order(2)
    public void testCreateValidEmployee() {
        // Arrange
        AccountRequestDto employee = new AccountRequestDto(EMPLOYEE_EMAIL, EMPLOYEE_USERNAME, EMPLOYEE_PASSWORD,
                EMPLOYEE_PHONENUM, EMPLOYEE_ADDRESS);

        // Act
        ResponseEntity<EmployeeResponseDto> response = client.postForEntity("/account/employee", employee,
                EmployeeResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeeResponseDto fetchedEmployee = response.getBody();
        assertNotNull(fetchedEmployee);

        assertEquals(EMPLOYEE_EMAIL, fetchedEmployee.getEmail());
        this.employeeEmail = fetchedEmployee.getEmail();
        assertEquals(EMPLOYEE_USERNAME, fetchedEmployee.getUsername());
        assertEquals(EMPLOYEE_PHONENUM, fetchedEmployee.getPhoneNumber());
        assertEquals(EMPLOYEE_ADDRESS, fetchedEmployee.getAddress());
        assertEquals(EmployeeStatus.Active, fetchedEmployee.getEmployeeStatus());
    }

    @Test
    @Order(3)
    public void testCreateValidCustomer() {
        // Arrange
        AccountRequestDto customer = new AccountRequestDto(CUSTOMER_EMAIL, CUSTOMER_USERNAME, CUSTOMER_PASSWORD,
                CUSTOMER_PHONENUM, CUSTOMER_ADDRESS);

        // Act
        ResponseEntity<AccountResponseDto> response = client.postForEntity("/account/customer", customer,
                AccountResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AccountResponseDto fetchedCustomer = response.getBody();
        assertNotNull(fetchedCustomer);
        assertEquals(CUSTOMER_EMAIL, fetchedCustomer.getEmail());
        this.customerEmail = fetchedCustomer.getEmail();
        assertEquals(CUSTOMER_USERNAME, fetchedCustomer.getUsername());
        assertEquals(CUSTOMER_PHONENUM, fetchedCustomer.getPhoneNumber());
        assertEquals(CUSTOMER_ADDRESS, fetchedCustomer.getAddress());
        assertEquals(AccountType.CUSTOMER, fetchedCustomer.getAccountType());
    }

    @Test
    @Order(4)
    public void testGetValidCustomerByEmail() {

        // Arrange
        String url = String.format("/account/customer/%s", this.customerEmail);
        // Act: call the GET endpoint
        ResponseEntity<AccountResponseDto> response = client.getForEntity(url, AccountResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AccountResponseDto fetchedCustomer = response.getBody();
        assertNotNull(fetchedCustomer);
        assertEquals(this.customerEmail, fetchedCustomer.getEmail());
        assertEquals(CUSTOMER_USERNAME, fetchedCustomer.getUsername());
        assertEquals(CUSTOMER_ADDRESS, fetchedCustomer.getAddress());
        assertEquals(CUSTOMER_PHONENUM, fetchedCustomer.getPhoneNumber());

    }

    @Test
    @Order(5)
    public void testGetValidEmployeeByEmail() {
        // Arrange
        String url = String.format("/account/employee/%s", this.employeeEmail);
        // Act: call the GET endpoint
        ResponseEntity<EmployeeResponseDto> response = client.getForEntity(url, EmployeeResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        EmployeeResponseDto fetchedEmployee = response.getBody();
        assertNotNull(fetchedEmployee);
        assertEquals(this.employeeEmail, fetchedEmployee.getEmail());
        assertEquals(EMPLOYEE_USERNAME, fetchedEmployee.getUsername());
        assertEquals(EMPLOYEE_ADDRESS, fetchedEmployee.getAddress());
        assertEquals(EMPLOYEE_PHONENUM, fetchedEmployee.getPhoneNumber());
        assertEquals(EmployeeStatus.Active, fetchedEmployee.getEmployeeStatus());

    }

    @Test
    @Order(6)
    public void testGetValidManagerByEmail() {
        // Arrange
        String url = String.format("/account/getmanager");
        // Act: call the GET endpoint
        ResponseEntity<AccountResponseDto> response = client.getForEntity(url, AccountResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AccountResponseDto fetchedManager = response.getBody();
        assertNotNull(fetchedManager);
        assertEquals(this.managerEmail, fetchedManager.getEmail());
        assertEquals(MANAGER_USERNAME, fetchedManager.getUsername());
        assertEquals(MANAGER_ADDRESS, fetchedManager.getAddress());
        assertEquals(MANAGER_PHONENUM, fetchedManager.getPhoneNumber());

    }

    @Test
    @Order(7)
    public void testGetValidCustomers() {
        // Arrange: create a second customer
        AccountRequestDto customer = new AccountRequestDto(SECCUSTOMER_EMAIL, SECCUSTOMER_USERNAME,
                SECCUSTOMER_PASSWORD, SECCUSTOMER_PHONENUM, SECCUSTOMER_ADDRESS);
        ResponseEntity<AccountResponseDto> createResponse = client.postForEntity("/account/customer", customer,
                AccountResponseDto.class);
        // Assert not null for the customer
        assertNotNull(createResponse);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        // Call the GET all customers endpoint
        String url = String.format("/account/customers");
        ResponseEntity<AccountListDto> response = client.getForEntity(url, AccountListDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AccountListDto fetchedCustomers = response.getBody();
        assertNotNull(fetchedCustomers);
        List<AccountResponseDto> customerList = fetchedCustomers.getAccounts();
        assertNotNull(customerList);
        assertFalse(customerList.isEmpty(), "Customers were expected");
        assertTrue(customerList.size() == 2);

        // Check field of the first customer that was created in previous test
        boolean found = false;
        for (AccountResponseDto firstCustomer : customerList) {
            if (firstCustomer.getEmail().equals(CUSTOMER_EMAIL)) {
                found = true;
                assertNotNull(firstCustomer);
                assertEquals(CUSTOMER_EMAIL, firstCustomer.getEmail());
                assertEquals(CUSTOMER_USERNAME, firstCustomer.getUsername());
                assertEquals(CUSTOMER_ADDRESS, firstCustomer.getAddress());
                assertEquals(CUSTOMER_PHONENUM, firstCustomer.getPhoneNumber());
                break;
            }
        }
        // Assert
        assertTrue(found);
        // Check fields of customer that was created in this test
        found = false;
        for (AccountResponseDto secondCustomer : customerList) {
            if (secondCustomer.getEmail().equals(SECCUSTOMER_EMAIL)) {
                found = true;
                assertNotNull(secondCustomer);
                assertEquals(SECCUSTOMER_EMAIL, secondCustomer.getEmail());
                assertEquals(SECCUSTOMER_USERNAME, secondCustomer.getUsername());
                assertEquals(SECCUSTOMER_ADDRESS, secondCustomer.getAddress());
                assertEquals(SECCUSTOMER_PHONENUM, secondCustomer.getPhoneNumber());
                break;
            }
        }
        // Assert
        assertTrue(found);

    }

    @Test
    @Order(8)
    public void testGetValidEmployees() {
        // Arrange: Create a second employee
        AccountRequestDto employee = new AccountRequestDto(SECEMPLOYEE_EMAIL, SECEMPLOYEE_USERNAME,
                SECEMPLOYEE_PASSWORD, SECEMPLOYEE_PHONENUM, SECEMPLOYEE_ADDRESS);
        ResponseEntity<EmployeeResponseDto> createResponse = client.postForEntity("/account/employee", employee,
                EmployeeResponseDto.class);
        assertNotNull(createResponse);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        // call the Get all employees endpoint
        String url = String.format("/account/employees");
        ResponseEntity<EmployeeListDto> response = client.getForEntity(url, EmployeeListDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Assert
        EmployeeListDto fetchedEmployees = response.getBody();
        assertNotNull(fetchedEmployees);
        List<EmployeeResponseDto> employeeList = fetchedEmployees.getAccounts();
        assertNotNull(employeeList);
        assertFalse(employeeList.isEmpty(), "Employees were expected");
        assertTrue(employeeList.size() == 2);

        EmployeeResponseDto firstEmployee = null;

        for (EmployeeResponseDto emp : employeeList) {
            if (emp.getEmail().equals(EMPLOYEE_EMAIL)) {
                firstEmployee = emp;
                break;
            }
        }

        assertNotNull(firstEmployee);
        assertEquals(EMPLOYEE_EMAIL, firstEmployee.getEmail());
        this.employeeEmail = firstEmployee.getEmail();
        assertEquals(EMPLOYEE_USERNAME, firstEmployee.getUsername());
        assertEquals(EMPLOYEE_ADDRESS, firstEmployee.getAddress());
        assertEquals(EMPLOYEE_PHONENUM, firstEmployee.getPhoneNumber());

        EmployeeResponseDto secondEmployee = null;

        for (EmployeeResponseDto secEmp : employeeList) {
            if (secEmp.getEmail().equals(SECEMPLOYEE_EMAIL)) {
                secondEmployee = secEmp;
                break;
            }
        }

        assertNotNull(secondEmployee);
        assertEquals(SECEMPLOYEE_EMAIL, secondEmployee.getEmail());
        this.secondEmployeeEmail = secondEmployee.getEmail();
        assertEquals(SECEMPLOYEE_USERNAME, secondEmployee.getUsername());
        assertEquals(SECEMPLOYEE_ADDRESS, secondEmployee.getAddress());
        assertEquals(SECEMPLOYEE_PHONENUM, secondEmployee.getPhoneNumber());

    }

    @Test
    @Order(9)
    public void testUpdateValidAccount() {

        // Arrange: call the PUT archive endpoint with the employee email to archive
        String url = String.format("/account/%s", this.employeeEmail);
        AccountRequestDto updatedInfo = new AccountRequestDto(this.employeeEmail, UPDATE_EMPLOYEE_USERNAME,
                UPDATE_EMPLOYEE_PASSWORD, UPDATE_EMPLOYEE_PHONENUM, UPDATE_EMPLOYEE_ADDRESS);

        // Assert
        ResponseEntity<EmployeeResponseDto> response = client.exchange(url, HttpMethod.PUT,
                new HttpEntity<>(updatedInfo), EmployeeResponseDto.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeeResponseDto updatedAccount = response.getBody();
        assertNotNull(updatedAccount);

        assertEquals(this.employeeEmail, updatedAccount.getEmail());
        assertEquals(UPDATE_EMPLOYEE_USERNAME, updatedAccount.getUsername());
        assertEquals(UPDATE_EMPLOYEE_ADDRESS, updatedAccount.getAddress());
        assertEquals(UPDATE_EMPLOYEE_PHONENUM, updatedAccount.getPhoneNumber());
    }

    @Test
    @Order(10)
    public void testArchiveValidEmployee() {

        // Arrange: call the PUT archive endpoint with the employee email to archive
        String url = String.format("/account/employee/%s", this.secondEmployeeEmail);

        ResponseEntity<EmployeeResponseDto> response = client.exchange(url, HttpMethod.PUT, null,
                EmployeeResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeeResponseDto archivedEmployee = response.getBody();
        assertNotNull(archivedEmployee);

        assertEquals(this.secondEmployeeEmail, archivedEmployee.getEmail());
        assertEquals(SECEMPLOYEE_USERNAME, archivedEmployee.getUsername());
        assertEquals(SECEMPLOYEE_ADDRESS, archivedEmployee.getAddress());
        assertEquals(SECEMPLOYEE_PHONENUM, archivedEmployee.getPhoneNumber());

        assertTrue(archivedEmployee.getEmployeeStatus() == EmployeeStatus.Archived);
    }

    @Test
    @Order(11)
    public void testCreateCustomerWithInvalidEmail() {
        // Arrange
        AccountRequestDto customer = new AccountRequestDto("", CUSTOMER_USERNAME, CUSTOMER_PASSWORD, CUSTOMER_PHONENUM,
                CUSTOMER_ADDRESS);

        // Act
        ResponseEntity<AccountResponseDto> response = client.postForEntity("/account/customer", customer,
                AccountResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(12)
    public void testCreateCustomerWithInvalidUsername() {
        // Arrange
        AccountRequestDto customer = new AccountRequestDto(CUSTOMER_EMAIL, "", CUSTOMER_PASSWORD, CUSTOMER_PHONENUM,
                CUSTOMER_ADDRESS);

        // Act
        ResponseEntity<AccountResponseDto> response = client.postForEntity("/account/customer", customer,
                AccountResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(13)
    public void testCreateCustomerWithInvalidPassword() {
        // Arrange
        AccountRequestDto customer = new AccountRequestDto(CUSTOMER_EMAIL, CUSTOMER_USERNAME, "", CUSTOMER_PHONENUM,
                CUSTOMER_ADDRESS);

        // Act
        ResponseEntity<AccountResponseDto> response = client.postForEntity("/account/customer", customer,
                AccountResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(14)
    public void testGetEmployeeWithNonExistentEmail() {
        // Arrange
        String url = String.format("/account/employee/%s", "email@notregistered.com");
        // Act
        ResponseEntity<EmployeeResponseDto> response = client.getForEntity(url, EmployeeResponseDto.class);
        // Assert
        assertNotNull(response);
        // Check status code
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(15)
    public void testGetCustomerWishList() {
        // Arrange
        String url = String.format("/account/customer/%s/wishlist", this.customerEmail);
        // Act
        ResponseEntity<WishListResponseDto> response = client.getForEntity(url, WishListResponseDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        WishListResponseDto fetchedWishList = response.getBody();
        assertNotNull(fetchedWishList);

    }

}
