package com.mcgill.ecse321.GameShop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.PlatformRepository;

import jakarta.transaction.Transactional;

@Service
public class PlatformService {
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    private GameRepository gameRepository;

    @Transactional
    public Platform createPlatform(String platformName, String managerEmail) {
        // Validate platform name
        if (platformName == null || platformName.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Platform name cannot be empty or null");
        }
        // Validate manager email
        if (managerEmail == null || managerEmail.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Manager email cannot be empty or null");
        }

        // Find manager by email
        Manager manager = managerRepository.findByEmail(managerEmail);
        if (manager == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND,
                    String.format("There is no manager with email: %s", managerEmail));
        }
        // Create and save new platform
        Platform platform = new Platform(platformName, manager);
        return platformRepository.save(platform);
    }

    @Transactional
    public Platform getPlatform(int platformId) {
        // Validate platform ID
        if (platformId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Platform ID must be greater than 0");
        }
        // Find platform by ID
        Platform platform = platformRepository.findById(platformId);
        if (platform == null) {
            throw new GameShopException(HttpStatus.NOT_FOUND, "Platform does not exist");
        }
        return platform;
    }

    @Transactional
    public Iterable<Platform> getAllPlatforms() {
        // Retrieve all platforms
        return platformRepository.findAll();
    }

    @Transactional
    public Platform updatePlatform(int platformId, String platformName) {
        // Validate platform ID
        if (platformId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Invalid platform ID");
        }
        // Validate platform name
        if (platformName == null || platformName.trim().isEmpty()) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Platform name cannot be empty or null");
        }

        // Retrieve platform by ID
        Platform platform = getPlatform(platformId);

        // Update platform name
        platform.setPlatformName(platformName);
        return platformRepository.save(platform);
    }

    @Transactional
    public void deletePlatform(int platformId) {
        // Validate platform ID
        if (platformId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Invalid platform ID");
        }
        // Retrieve platform by ID
        Platform platform = getPlatform(platformId);
        // Find all games associated with the platform
        List<Game> gamesInPlatform = gameRepository.findAllByPlatformsContains(platform);
        for (Game game : gamesInPlatform) {
            // Remove platform from each game
            List<Platform> platforms = new ArrayList<>(game.getPlatforms());
            platforms.remove(platform);
            game.setPlatforms(platforms);

            // Save updated game
            gameRepository.save(game);
        }

        // Delete platform
        platformRepository.delete(platform);
    }

    @Transactional
    public List<Game> getAllGamesInPlatform(int platformId) {
        // Validate platform ID
        if (platformId <= 0) {
            throw new GameShopException(HttpStatus.BAD_REQUEST, "Invalid platform ID");
        }
        // Retrieve platform by ID
        Platform platform = getPlatform(platformId);
        // Find all games associated with the platform
        List<Game> games = gameRepository.findAllByPlatformsContains(platform);
        return games;
    }
}
