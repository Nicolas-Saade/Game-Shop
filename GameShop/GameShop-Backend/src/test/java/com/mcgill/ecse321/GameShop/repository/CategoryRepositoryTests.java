package com.mcgill.ecse321.GameShop.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Manager;

import jakarta.transaction.Transactional;

@SpringBootTest
public class CategoryRepositoryTests {

    // private static List<Integer> testCategoryIDs = new ArrayList<Integer>();
    private static List<String> testEmails = new ArrayList<String>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ManagerRepository managerRepository;

    // Clear the database before and after each test to ensure a clean state
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        categoryRepository.deleteAll();
        managerRepository.deleteAll();
        // Category.clearTestCategories(CategoryRepositoryTests.testCategoryIDs);
        // CategoryRepositoryTests.testCategoryIDs.clear();
        Account.clearTestEmails(CategoryRepositoryTests.testEmails);
        CategoryRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    public void testCreateAndReadCategory() {

        // Create a new Manager instance
        String email = "5@5.com";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9291";
        String address = "1234 rue Sainte-Catherine";

        Manager createdManager = new Manager(email, username, password, phoneNumber, address);
        createdManager = managerRepository.save(createdManager);
        CategoryRepositoryTests.testEmails.add(createdManager.getEmail());

        // Create and save the first Category
        Category firstCreatedCategory = new Category("thriller", createdManager);
        firstCreatedCategory = categoryRepository.save(firstCreatedCategory);
        int firstCategoryId = firstCreatedCategory.getCategory_id();
        // CategoryRepositoryTests.testCategoryIDs.add(firstCreatedCategory.getCategory_id());

        // Retrieve the first Category by its ID
        Category firstPulledCategory = categoryRepository.findById(firstCategoryId);

        // Create and save the second Category
        Category secondCreatedCategory = new Category("barbie", createdManager);
        secondCreatedCategory = categoryRepository.save(secondCreatedCategory);
        int secondCategoryId = secondCreatedCategory.getCategory_id();
        // CategoryRepositoryTests.testCategoryIDs.add(secondCreatedCategory.getCategory_id());

        // Retrieve the second Category by its ID
        Category secondPulledCategory = categoryRepository.findById(secondCategoryId);

        // Assertions to verify the correctness of the operations
        assertNotNull(firstPulledCategory);
        assertNotNull(secondCreatedCategory);
        assertEquals(firstCreatedCategory.getCategory_id(), firstPulledCategory.getCategory_id());
        assertEquals(secondCreatedCategory.getCategory_id(), secondPulledCategory.getCategory_id());

        assertEquals("thriller", firstPulledCategory.getCategoryName());
        assertEquals("barbie", secondPulledCategory.getCategoryName());

        assertEquals(createdManager.getEmail(), firstPulledCategory.getManager().getEmail());
        assertEquals(createdManager.getEmail(), secondPulledCategory.getManager().getEmail());

        // Verify that there are exactly 2 categories in the repository
        List<Category> category = (List<Category>) categoryRepository.findAll();
        assertEquals(2, category.size());
    }

}
