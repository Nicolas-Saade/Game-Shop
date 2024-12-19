package com.mcgill.ecse321.GameShop.repository;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.mcgill.ecse321.GameShop.model.Account;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Platform;

import jakarta.transaction.Transactional;

@SpringBootTest
public class PlatformRepositoryTests {

    private static List<String> testEmails = new ArrayList<String>();      

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Clear the database before and after each test
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
        platformRepository.deleteAll();
        accountRepository.deleteAll();
        Account.clearTestEmails(PlatformRepositoryTests.testEmails);
        PlatformRepositoryTests.testEmails.clear();
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void testCreateAndReadPlatform() {
        // Create Manager
        String email = "anthony.saber@hotmail.cox";
        String username = "AnthonySaber";
        String password = "password";
        String phoneNumber = "+1 (438) 865-9290";
        String address = "1234 rue Sainte-Catherine";

        Manager createdManager = new Manager(email, username, password, phoneNumber, address);
        createdManager = accountRepository.save(createdManager);
        PlatformRepositoryTests.testEmails.add(createdManager.getEmail());

        // Create Platform
        Platform createdPlatform = new Platform("PS5", createdManager);
        createdPlatform = platformRepository.save(createdPlatform);
        int platformId = createdPlatform.getPlatform_id();

        // Retrieve Platform
        Platform pulledPlatform = platformRepository.findById(platformId);

        // Assertions to verify the platform was created and retrieved correctly
        assertNotNull(pulledPlatform);
        assertNotNull(createdPlatform);
        assertEquals("PS5", pulledPlatform.getPlatformName());
        assertEquals(createdManager.getEmail(), pulledPlatform.getManager().getEmail());
    }

    @Test
    @Transactional
    public void testUpdatePlatformName() {
        // Create Manager
        String email = "manager7@example.com";
        String username = "ManagerSeven";
        String password = "password";
        String phoneNumber = "+1 (789) 012-3456";
        String address = "700 rue Manager";

        Manager manager = new Manager(email, username, password, phoneNumber, address);
        manager = accountRepository.save(manager);
        PlatformRepositoryTests.testEmails.add(manager.getEmail());

        // Create Platform
        Platform platform = new Platform("OldName", manager);
        platform = platformRepository.save(platform);

        // Update Platform Name
        platform.setPlatformName("NewName");
        platform = platformRepository.save(platform);

        // Retrieve Platform
        Platform pulledPlatform = platformRepository.findById(platform.getPlatform_id());

        // Assertions to verify the platform name was updated correctly
        assertNotNull(pulledPlatform, "Platform should not be null.");
        assertEquals("NewName", pulledPlatform.getPlatformName(), "Platform name should be updated.");
    }
}
