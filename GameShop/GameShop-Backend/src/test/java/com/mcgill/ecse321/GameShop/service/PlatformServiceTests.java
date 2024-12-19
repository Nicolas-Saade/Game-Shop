package com.mcgill.ecse321.GameShop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.model.Platform;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;
import com.mcgill.ecse321.GameShop.repository.PlatformRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PlatformServiceTests {

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PlatformService platformService;

    private static final int VALID_PLATFORM_ID = 98;
    private static final String VALID_PLATFORM_NAME = "PlayStation 5";
    private static final String UPDATED_PLATFORM_NAME = "PlayStation 5 Pro";
    private static final String VALID_MANAGER_EMAIL = "managerrr@example.com";

    @Test
    public void testCreateValidPlatform() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL, "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        // Mock the manager repository to return the manager
        when(managerRepository.findByEmail(VALID_MANAGER_EMAIL)).thenReturn(manager);

        // Mock the platform repository to return the platform with an ID after saving
        when(platformRepository.save(any(Platform.class))).thenAnswer((InvocationOnMock invocation) -> {
            Platform platform = invocation.getArgument(0);
            platform.setPlatform_id(VALID_PLATFORM_ID); // Simulate setting the ID after saving
            return platform;
        });

        // Act
        Platform createdPlatform = platformService.createPlatform(VALID_PLATFORM_NAME, VALID_MANAGER_EMAIL);

        // Assert
        assertNotNull(createdPlatform);
        assertEquals(VALID_PLATFORM_ID, createdPlatform.getPlatform_id());
        assertEquals(VALID_PLATFORM_NAME, createdPlatform.getPlatformName());
        assertEquals(manager, createdPlatform.getManager());

        // Verify that the save method was called once
        verify(platformRepository, times(1)).save(createdPlatform);
        verify(managerRepository, times(1)).findByEmail(VALID_MANAGER_EMAIL);
    }

    @Test
    public void testCreatePlatformWithNullPlatformName() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.createPlatform(null, VALID_MANAGER_EMAIL);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platform name cannot be empty or null", exception.getMessage());
        verify(platformRepository, never()).save(any(Platform.class));
    }

    public void testCreatePlatformWithEmptyPlatformName() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.createPlatform("  ", VALID_MANAGER_EMAIL);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platform name cannot be empty or null", exception.getMessage());
        verify(platformRepository, never()).save(any(Platform.class));

    }

    @Test
    public void testCreatePlatformWithNullManagerEmail() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.createPlatform(VALID_PLATFORM_NAME, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Manager email cannot be empty or null", exception.getMessage());
        verify(platformRepository, never()).save(any(Platform.class));
    }

    @Test
    public void testCreatePlatformWithEmptyManagerEmail() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.createPlatform(VALID_PLATFORM_NAME, "  ");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Manager email cannot be empty or null", exception.getMessage());
    
        verify(platformRepository, never()).save(any(Platform.class));}

    @Test
    public void testCreatePlatformWithNonExistentManager() {
        // Arrange
        // Mock the manager repository to return null (manager not found)
        when(managerRepository.findByEmail("invalidManager@gmil.com")).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.createPlatform(VALID_PLATFORM_NAME, "invalidManager@gmil.com");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no manager with email: invalidManager@gmil.com", exception.getMessage());
   
        verify(platformRepository, never()).save(any(Platform.class));}

    @Test
    public void testGetPlatformByValidId() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "s", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        // Mock the manager repository to return the manager
        // when(managerRepository.findByEmail(VALID_MANAGER_EMAIL)).thenReturn(manager);
        Platform platform = new Platform(VALID_PLATFORM_NAME, manager);
        platform.setPlatform_id(94);

        when(platformRepository.findById(94)).thenReturn(platform);

        // // Act
        Platform foundPlatform = platformService.getPlatform(94);

        // Assert
        assertNotNull(foundPlatform);
        assertEquals(94, foundPlatform.getPlatform_id(), "Platform ID does not match");
        assertEquals(VALID_PLATFORM_NAME, foundPlatform.getPlatformName());
        assertEquals(manager, foundPlatform.getManager());
        verify(platformRepository, times(1)).findById(94);
    }

    @Test
    public void testGetPlatformByInvalidId() {
        // Arrange
        // Mock the platform repository to return empty (platform not found)
        int invalidId = 7899;
        when(platformRepository.findById(invalidId)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.getPlatform(invalidId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Platform does not exist", exception.getMessage());
        verify(platformRepository, times(1)).findById(invalidId);
    }
    @Test
    public void testGetPlatformByInvalidIdValue(){
        // Arrange
        int invalidId = -1;
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.getPlatform(invalidId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platform ID must be greater than 0", exception.getMessage());
        verify(platformRepository, never()).findById(invalidId);
    }

    @Test
    public void testGetAllPlatforms() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "oiu", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");
        Platform platform1 = new Platform(VALID_PLATFORM_NAME, manager);
        platform1.setPlatform_id(11);
        Platform platform2 = new Platform("Xbox Series X", manager);
        platform2.setPlatform_id(22);
        List<Platform> platformsArray = Arrays.asList(platform1, platform2);
        when(platformRepository.findAll()).thenReturn(platformsArray);

        // Act
        Iterable<Platform> platforms = platformService.getAllPlatforms();

        // Assert
        assertNotNull(platforms);
        List<Platform> platformList = new ArrayList<>();
        platforms.forEach(platformList::add);
        assertTrue(platformList.contains(platform1));
        assertTrue(platformList.contains(platform2));
        assertEquals(2, platformList.size());
        assertEquals(11, platformList.get(0).getPlatform_id());
        assertEquals(VALID_PLATFORM_NAME, platformList.get(0).getPlatformName());
        assertEquals(manager, platformList.get(0).getManager());
        assertEquals(22, platformList.get(1).getPlatform_id());
        assertEquals("Xbox Series X", platformList.get(1).getPlatformName());
        assertEquals(manager, platformList.get(1).getManager());
        verify(platformRepository, times(1)).findAll();
    }

    @Test
    public void testUpdatePlatform() {

        Manager manager = new Manager(VALID_MANAGER_EMAIL + "ss", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        // Mock the platform repository to find the existing platform
        Platform platform = new Platform(VALID_PLATFORM_NAME, manager);
        platform.setPlatform_id(13);

        when(platformRepository.findById(13)).thenReturn(platform);

        // Mock the platform repository to return the updated platform after saving
        when(platformRepository.save(any(Platform.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        Platform updatedPlatform = platformService.updatePlatform(13, UPDATED_PLATFORM_NAME);

        // Assert
        assertNotNull(updatedPlatform);
        assertEquals(13, updatedPlatform.getPlatform_id());
        assertEquals(UPDATED_PLATFORM_NAME, updatedPlatform.getPlatformName());
        assertEquals(manager, updatedPlatform.getManager());

        // Verify that the save method was called once
        verify(platformRepository, times(1)).save(any(Platform.class));
        verify(platformRepository, times(1)).findById(13);
    }

    @Test
    public void testUpdatePlatformWithInvalidId() {
        // Arrange
        // Mock the platform repository to return empty (platform not found)
        int invalidId = 9877;
        when(platformRepository.findById(invalidId)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.updatePlatform(invalidId, UPDATED_PLATFORM_NAME);
        });


        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Platform does not exist", exception.getMessage());
        verify(platformRepository, never()).save(any(Platform.class));
        verify(platformRepository, times(1)).findById(invalidId);
    }

    @Test
    public void testUpdatePlatformWithNullPlatformName() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "sdaass", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");
        Platform platform = new Platform(VALID_PLATFORM_NAME, manager);
        platform.setPlatform_id(30);
        // when(platformRepository.findById(30)).thenReturn(platform);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.updatePlatform(30, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platform name cannot be empty or null", exception.getMessage());
    verify(platformRepository, never()).save(any(Platform.class));
    }

    @Test
    public void testUpdatePlatformWithEmptyPlatformName() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "sdss", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");
        Platform platform = new Platform(VALID_PLATFORM_NAME, manager);
        platform.setPlatform_id(307);
        // when(platformRepository.findById(307)).thenReturn(platform);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.updatePlatform(307, "  ");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Platform name cannot be empty or null", exception.getMessage());
        verify(platformRepository, never()).save(any(Platform.class));
}

    @Test
    public void testDeletePlatform() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "sdssa", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        Platform platform = new Platform(VALID_PLATFORM_NAME, manager);
        platform.setPlatform_id(14);

        // Mock the platform repository to find the platform
        when(platformRepository.findById(14)).thenReturn(platform);

        // Act
        platformService.deletePlatform(14);

        // Assert
        // Verify that the delete method was called once
        verify(platformRepository, times(1)).delete(platform);
        verify(platformRepository, times(1)).findById(14);
    }

    @Test
    public void testDeletePlatformWithInvalidId() {
        // Arrange
        // Mock the platform repository to return empty (platform not found)
        int invalidId = 906;
        when(platformRepository.findById(invalidId)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.deletePlatform(invalidId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Platform does not exist", exception.getMessage());
    }
    @Test
    public void testDeletePlatformWithInvalidIdValue(){
        // Arrange
        int invalidId = -1;
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            platformService.deletePlatform(invalidId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid platform ID", exception.getMessage());
        verify(platformRepository, never()).findById(invalidId);
        verify(platformRepository, never()).delete(any(Platform.class));
    }
    @Test
public void testGetAllGamesInPlatform_Successful() {
    // Arrange
    Manager manager = new Manager(VALID_MANAGER_EMAIL+"qwsa", "managerUser", "managerPass", "123-456-7890", "123 Manager Street");
    Platform platform = new Platform(VALID_PLATFORM_NAME, manager);
    platform.setPlatform_id(70);

    Game game1 = new Game("Game Title 1", "Description 1", 50, Game.GameStatus.InStock, 100, "http://example.com/image1.jpg");
    Game game2 = new Game("Game Title 2", "Description 2", 60, Game.GameStatus.InStock, 50, "http://example.com/image2.jpg");

    when(platformRepository.findById(70)).thenReturn(platform);
    when(gameRepository.findAllByPlatformsContains(platform)).thenReturn(Arrays.asList(game1, game2));

    // Act
    List<Game> gamesInPlatform = platformService.getAllGamesInPlatform(70);

    // Assert
    assertNotNull(gamesInPlatform);
    assertEquals(2, gamesInPlatform.size());
    assertTrue(gamesInPlatform.contains(game1));
    assertTrue(gamesInPlatform.contains(game2));
    verify(platformRepository, times(1)).findById(70);
    verify(gameRepository, times(1)).findAllByPlatformsContains(platform);
}
@Test
public void testGetAllGamesInPlatformWithInvalidId() {
    // Arrange
    int invalidPlatformId = 999;
    when(platformRepository.findById(invalidPlatformId)).thenReturn(null);

    // Act & Assert
    GameShopException exception = assertThrows(GameShopException.class, () -> {
        platformService.getAllGamesInPlatform(invalidPlatformId);
    });

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Platform does not exist", exception.getMessage());
    verify(platformRepository, times(1)).findById(invalidPlatformId);
    verify(gameRepository, never()).findAllByPlatformsContains(any(Platform.class));
}
@Test
public void testGetAllGamesInPlatformWithInvalidIdValue() {
    // Arrange
    int invalidPlatformId = -1;

    // Act & Assert
    GameShopException exception = assertThrows(GameShopException.class, () -> {
        platformService.getAllGamesInPlatform(invalidPlatformId);
    });

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Invalid platform ID", exception.getMessage());
    verify(platformRepository, never()).findById(invalidPlatformId);
    verify(gameRepository, never()).findAllByPlatformsContains(any(Platform.class));
}

@Test
public void testDeletePlatformWithAssociatedGames(){
    // Arrange
    Manager manager = new Manager(VALID_MANAGER_EMAIL + "assocGames", "managerUser", "managerPass", "123-456-7890", "123 Manager Street");
    Platform platform = new Platform("PlayStation 5", manager);
    platform.setPlatform_id(79);

    Game game1 = new Game("Game1", "Description1", 60, GameStatus.InStock, 5, "photo1.jpg");
    game1.addPlatform(platform);
    Game game2 = new Game("Game2", "Description2", 70, GameStatus.InStock, 10, "photo2.jpg");
    game2.addPlatform(platform);

    List<Game> gamesInPlatform = Arrays.asList(game1, game2);
    
    when(platformRepository.findById(79)).thenReturn(platform);
    when(gameRepository.findAllByPlatformsContains(platform)).thenReturn(gamesInPlatform);

    // Act
    platformService.deletePlatform(79);

    // Assert
    for (Game game : gamesInPlatform) {
        assertFalse(game.getPlatforms().contains(platform));
        verify(gameRepository, times(1)).save(game);
    }
    verify(platformRepository, times(1)).delete(platform);
    verify(platformRepository, times(1)).findById(79);
}



}
