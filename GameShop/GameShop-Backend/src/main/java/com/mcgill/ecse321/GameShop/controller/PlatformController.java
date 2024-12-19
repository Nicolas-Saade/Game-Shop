package com.mcgill.ecse321.GameShop.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mcgill.ecse321.GameShop.dto.GameDto.GameListDto;
import com.mcgill.ecse321.GameShop.dto.GameDto.GameSummaryDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformListDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformRequestDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformResponseDto;
import com.mcgill.ecse321.GameShop.dto.PlatformDto.PlatformSummaryDto;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.service.PlatformService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class PlatformController {

    @Autowired
    private PlatformService platformService;

    /**
     * Create a new platform.
     * 
     * @param request the platform request data transfer object containing the
     *                details of the platform to be created
     * @return the response data transfer object containing the details of the
     *         created platform
     */
    @PostMapping("/platforms")
    public PlatformResponseDto createPlatform(@Valid @RequestBody PlatformRequestDto request) {
        // Create the platform
        Platform platform = platformService.createPlatform(request.getPlatformName(), request.getManagerEmail());
        return PlatformResponseDto.create(platform);
    }

    /**
     * Get a platform by ID.
     * 
     * @param pid the ID of the platform to retrieve
     * @return the response data transfer object containing the details of the
     *         retrieved platform
     */
    @GetMapping("/platforms/{pid}")
    public PlatformResponseDto getPlatformById(@PathVariable int pid) {
        // Get the platform
        Platform platform = platformService.getPlatform(pid);
        return PlatformResponseDto.create(platform);
    }

    /**
     * Get all platforms.
     * 
     * @return a list data transfer object containing the summaries of all platforms
     */
    @GetMapping("/platforms")
    public PlatformListDto getAllPlatforms() {
        List<PlatformSummaryDto> dtos = new ArrayList<>();

        // Get all platforms and add them to dtos list
        for (Platform platform : platformService.getAllPlatforms()) {
            dtos.add(new PlatformSummaryDto(platform));
        }
        return new PlatformListDto(dtos);
    }

    /**
     * Get all games in a platform.
     * 
     * @param pid the ID of the platform to retrieve games from
     * @return a list data transfer object containing the summaries of all games in
     *         the platform
     */
    @GetMapping("/platforms/{pid}/games")
    public GameListDto getAllGamesInPlatform(@PathVariable int pid) {
        List<GameSummaryDto> dtos = new ArrayList<>();

        // Get all games in the platform and add them to dtos list
        for (Game game : platformService.getAllGamesInPlatform(pid)) {
            dtos.add(new GameSummaryDto(game));
        }
        return new GameListDto(dtos);
    }

    /**
     * Update a platform's name.
     * 
     * @param id      the ID of the platform to update
     * @param request the platform request data transfer object containing the
     *                updated name of the platform
     * @return the response data transfer object containing the details of the
     *         updated platform
     */
    @PutMapping("/platforms/{id}")
    public PlatformResponseDto updatePlatform(@PathVariable int id, @RequestBody PlatformRequestDto request) {
        // Update the platform name
        Platform platform = platformService.updatePlatform(id, request.getPlatformName());
        return PlatformResponseDto.create(platform);
    }

    /**
     * Delete a platform.
     * 
     * @param id the ID of the platform to delete
     */
    @DeleteMapping("/platforms/{id}")
    public void deletePlatform(@PathVariable int id) {
        platformService.deletePlatform(id);
    }
}
