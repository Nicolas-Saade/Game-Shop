package com.mcgill.ecse321.GameShop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.mcgill.ecse321.GameShop.exception.GameShopException;
import com.mcgill.ecse321.GameShop.model.Category;
import com.mcgill.ecse321.GameShop.model.Game;
import com.mcgill.ecse321.GameShop.model.Game.GameStatus;
import com.mcgill.ecse321.GameShop.model.Manager;
import com.mcgill.ecse321.GameShop.repository.CategoryRepository;
import com.mcgill.ecse321.GameShop.repository.GameRepository;
import com.mcgill.ecse321.GameShop.repository.ManagerRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private CategoryService categoryService;

    private static final int VALID_CATEGORY_ID = 1;
    private static final String VALID_CATEGORY_NAME = "Action Games";
    private static final String UPDATED_CATEGORY_NAME = "Adventure Games";
    private static final String VALID_MANAGER_EMAIL = "managerOfPlatform@example.com";
    private static final String INVALID_MANAGER_EMAIL = "invalid@example.com";
    private static final String SECOND_CATEGORY_NAME = "Strategy Games";

    // --- Tests for createCategory ---

    @Test
    public void testCreateValidCategory() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL, "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        // Mock the manager repository to return the manager
        when(managerRepository.findByEmail(VALID_MANAGER_EMAIL)).thenReturn(manager);
        when(categoryRepository.save(any(Category.class))).thenAnswer((InvocationOnMock invocation) -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setCategory_id(VALID_CATEGORY_ID);
            return savedCategory;
        });

        // Act
        Category createdCategory = categoryService.createCategory(VALID_CATEGORY_NAME, VALID_MANAGER_EMAIL);

        // Assert
        assertNotNull(createdCategory);
        assertEquals(VALID_CATEGORY_ID, createdCategory.getCategory_id());
        assertEquals(VALID_CATEGORY_NAME, createdCategory.getCategoryName());
        assertEquals(manager, createdCategory.getManager());

        verify(managerRepository, times(1)).findByEmail(VALID_MANAGER_EMAIL);
        verify(categoryRepository, times(1)).save(createdCategory);
    }

    @Test
    public void testCreateCategoryWithNullName() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.createCategory(null, VALID_MANAGER_EMAIL);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Category name cannot be empty or null", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testCreateCategoryWithEmptyName() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.createCategory("   ", VALID_MANAGER_EMAIL);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Category name cannot be empty or null", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testCreateCategoryWithNullManagerEmail() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.createCategory(VALID_CATEGORY_NAME, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Manager email cannot be empty or null", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testCreateCategoryWithEmptyManagerEmail() {
        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.createCategory(VALID_CATEGORY_NAME, "  ");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Manager email cannot be empty or null", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testCreateCategoryWithNonExistentManager() {
        // Arrange
        when(managerRepository.findByEmail(INVALID_MANAGER_EMAIL)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.createCategory(VALID_CATEGORY_NAME, INVALID_MANAGER_EMAIL);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("There is no manager with email: invalid@example.com", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    // --- Tests for getCategory ---

    @Test
    public void testGetCategoryByValidId() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "s", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        Category category = new Category(VALID_CATEGORY_NAME, manager);
        category.setCategory_id(94);

        when(categoryRepository.findById(94)).thenReturn(category);

        // Act
        Category foundCategory = categoryService.getCategory(94);

        // Assert
        assertNotNull(foundCategory);
        assertEquals(94, foundCategory.getCategory_id(), "Category ID does not match");
        assertEquals(VALID_CATEGORY_NAME, foundCategory.getCategoryName());
        assertEquals(manager, foundCategory.getManager());

        verify(categoryRepository, times(1)).findById(94);
    }

    @Test
    public void testGetCategoryByInvalidId() {
        // Arrange
        int invalidId = 999;
        when(categoryRepository.findById(invalidId)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.getCategory(invalidId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Category does not exist", exception.getMessage());

        verify(categoryRepository, times(1)).findById(invalidId);
    }

    @Test
    public void testGetCategoryWithInvalidIdValue() {
        // Arrange
        int invalidId = -1;

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.getCategory(invalidId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid category ID", exception.getMessage());

        verify(categoryRepository, never()).findById(anyInt());
    }

    // --- Tests for getAllCategories ---

    @Test
    public void testGetAllCategories() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "oiu", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        Category category1 = new Category(VALID_CATEGORY_NAME, manager);
        category1.setCategory_id(45);

        Category category2 = new Category(SECOND_CATEGORY_NAME, manager);
        category2.setCategory_id(46);

        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        Iterable<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        List<Category> resultList = new ArrayList<>();
        result.forEach(resultList::add);

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(category1));
        assertTrue(resultList.contains(category2));
        assertEquals(45, resultList.get(0).getCategory_id());
        assertEquals(46, resultList.get(1).getCategory_id());
        assertEquals(VALID_CATEGORY_NAME, resultList.get(0).getCategoryName());
        assertEquals(SECOND_CATEGORY_NAME, resultList.get(1).getCategoryName());
        assertEquals(manager, resultList.get(0).getManager());
        assertEquals(manager, resultList.get(1).getManager());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllCategoriesEmpty() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Iterable<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        List<Category> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertTrue(resultList.isEmpty());

        verify(categoryRepository, times(1)).findAll();
    }

    // --- Tests for getAllGamesInCategory ---

    @Test
    public void testGetAllGamesInCategory() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "oiud", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        Category category = new Category(VALID_CATEGORY_NAME, manager);
        category.setCategory_id(7);

        // Create two games in the category
        Game game1 = new Game("game1", "String aDescription of game 1", 7, GameStatus.InStock, 7, "phoyourkl");
        game1.setGame_id(7);
        game1.addCategory(category);

        Game game2 = new Game("game2", "String aDescription of game 2", 7, GameStatus.InStock, 6, "phoyofghurkl");
        game2.setGame_id(8);
        game2.addCategory(category);

        List<Game> games = Arrays.asList(game1, game2);

        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(category);
        when(gameRepository.findAllByCategoriesContains(category)).thenReturn(games);

        // Act
        List<Game> result = categoryService.getAllGamesInCategory(VALID_CATEGORY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(game1));
        assertTrue(result.contains(game2));
        assertEquals(7, result.get(0).getGame_id());
        assertEquals(8, result.get(1).getGame_id());
        assertEquals("game1", result.get(0).getTitle());
        assertEquals("game2", result.get(1).getTitle());

        verify(categoryRepository, times(1)).findById(VALID_CATEGORY_ID);

    }

    @Test
    public void testGetAllGamesInCategoryWithNoGames() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "oiuda", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        Category category = new Category(VALID_CATEGORY_NAME, manager);
        category.setCategory_id(6);

        List<Game> games = new ArrayList<>();

        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(category);
        when(gameRepository.findAllByCategoriesContains(category)).thenReturn(games);

        // Act
        List<Game> result = categoryService.getAllGamesInCategory(VALID_CATEGORY_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findById(VALID_CATEGORY_ID);

    }

    @Test
    public void testGetAllGamesInCategoryWithInvalidId() {
        // Arrange
        int invalidId = 999;
        when(categoryRepository.findById(invalidId)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.getAllGamesInCategory(invalidId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Category does not exist", exception.getMessage());

        verify(categoryRepository, times(1)).findById(invalidId);
    }

    // --- Tests for updateCategory ---

    @Test
    public void testUpdateCategory() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "sssssaaws", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        // Mock the platform repository to find the existing platform
        Category category = new Category(VALID_CATEGORY_NAME, manager);
        category.setCategory_id(13);

        when(categoryRepository.findById(13)).thenReturn(category);
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        Category updatedCategory = categoryService.updateCategory(13, UPDATED_CATEGORY_NAME);

        // Assert
        assertNotNull(updatedCategory);
        assertEquals(13, updatedCategory.getCategory_id());
        assertEquals(UPDATED_CATEGORY_NAME, updatedCategory.getCategoryName());
        assertEquals(manager, updatedCategory.getManager());

        verify(categoryRepository, times(1)).findById(13);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testUpdateCategoryWithInvalidId() {
        // Arrange
        int invalidId = 9998;
        when(categoryRepository.findById(invalidId)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.updateCategory(invalidId, UPDATED_CATEGORY_NAME);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Category does not exist", exception.getMessage());

        verify(categoryRepository, times(1)).findById(invalidId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testUpdateCategoryWithNullName() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "sdvbn1374ss", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        Category category = new Category(VALID_CATEGORY_NAME, manager);
        category.setCategory_id(30);

        when(categoryRepository.findById(30)).thenReturn(category);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.updateCategory(30, null);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Category name cannot be empty or null", exception.getMessage());

        verify(categoryRepository, times(1)).findById(30);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testUpdateCategoryWithEmptyName() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "sdss", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");

        Category category = new Category(VALID_CATEGORY_NAME, manager);
        category.setCategory_id(14);

        when(categoryRepository.findById(14)).thenReturn(category);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.updateCategory(14, "  ");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Category name cannot be empty or null", exception.getMessage());

        verify(categoryRepository, times(1)).findById(14);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // --- Tests for deleteCategory ---

    @Test
    public void testDeleteCategory() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "sdssa", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");
        Category category = new Category("platformName", manager);
        category.setCategory_id(14);

        when(categoryRepository.findById(14)).thenReturn(category);

        // Act
        categoryService.deleteCategory(14);

        // Assert
        verify(categoryRepository, times(1)).findById(14);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    public void testDeleteCategoryWithInvalidId() {
        // Arrange
        int invalidId = 999;
        when(categoryRepository.findById(invalidId)).thenReturn(null);

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.deleteCategory(invalidId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Category does not exist", exception.getMessage());

        verify(categoryRepository, times(1)).findById(invalidId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    public void testDeleteCategoryWithInvalidIdValue() {
        // Arrange
        int invalidId = -1;

        // Act & Assert
        GameShopException exception = assertThrows(GameShopException.class, () -> {
            categoryService.deleteCategory(invalidId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid category ID", exception.getMessage());

        verify(categoryRepository, never()).findById(anyInt());
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    public void testDeleteCategoryWithAssociatedGames() {
        // Arrange
        Manager manager = new Manager(VALID_MANAGER_EMAIL + "assocGames", "managerUser", "managerPass", "123-456-7890",
                "123 Manager Street");
        Category category = new Category("Action", manager);
        category.setCategory_id(20);

        Game game1 = new Game("Game1", "Description1", 60, GameStatus.InStock, 5, "photo1.jpg");
        game1.addCategory(category);
        Game game2 = new Game("Game2", "Description2", 70, GameStatus.InStock, 10, "photo2.jpg");
        game2.addCategory(category);

        List<Game> gamesInCategory = Arrays.asList(game1, game2);

        when(categoryRepository.findById(20)).thenReturn(category);
        when(gameRepository.findAllByCategoriesContains(category)).thenReturn(gamesInCategory);

        // Act
        categoryService.deleteCategory(20);

        // Assert
        for (Game game : gamesInCategory) {
            assertFalse(game.getCategories().contains(category));
            verify(gameRepository, times(1)).save(game);
        }
        verify(categoryRepository, times(1)).delete(category);
        verify(categoryRepository, times(1)).findById(20);
    }
}