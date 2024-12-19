/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import java.util.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

// line 69 "../../../../../../model.ump"
// line 234 "../../../../../../model.ump"
@Entity
public class Game {

  // ------------------------
  // ENUMERATIONS
  // ------------------------

  public enum GameStatus {
    InStock, OutOfStock, Archived
  }

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, Game> gamesByGame_id = new HashMap<Integer, Game>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Game Attributes
  @Id
  @GeneratedValue
  private int game_id;
  private String title;
  private String description;
  private double price;

  @Enumerated(EnumType.STRING)
  private GameStatus gameStatus;

  private int stockQuantity;
  private String photoUrl;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "game_category_jt", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
  private List<Category> categories;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "game_platform_jt", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "platform_id"))
  private List<Platform> platforms;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Game(String aTitle, String aDescription, double aPrice, GameStatus aGameStatus, int aStockQuantity,
      String aPhotoUrl) {
    title = aTitle;
    description = aDescription;
    price = aPrice;
    gameStatus = aGameStatus;
    stockQuantity = aStockQuantity;
    photoUrl = aPhotoUrl;
    categories = new ArrayList<Category>();
    platforms = new ArrayList<Platform>();
  }

  protected Game() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setGame_id(int aGame_id) {
    boolean wasSet = false;
    Integer anOldGame_id = getGame_id();
    if (anOldGame_id != null && anOldGame_id.equals(aGame_id)) {
      return true;
    }
    if (hasWithGame_id(aGame_id)) {
      return wasSet;
    }
    game_id = aGame_id;
    wasSet = true;
    if (anOldGame_id != null) {
      gamesByGame_id.remove(anOldGame_id);
    }
    gamesByGame_id.put(aGame_id, this);
    return wasSet;
  }

  public boolean setTitle(String aTitle) {
    boolean wasSet = false;
    title = aTitle;
    wasSet = true;
    return wasSet;
  }

  public boolean setDescription(String aDescription) {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public boolean setPrice(double aPrice) {
    boolean wasSet = false;
    price = aPrice;
    wasSet = true;
    return wasSet;
  }

  public boolean setGameStatus(GameStatus aGameStatus) {
    boolean wasSet = false;
    gameStatus = aGameStatus;
    wasSet = true;
    return wasSet;
  }

  public boolean setStockQuantity(int aStockQuantity) {
    boolean wasSet = false;
    stockQuantity = aStockQuantity;
    wasSet = true;
    return wasSet;
  }

  public boolean setPhotoUrl(String aPhotoUrl) {
    boolean wasSet = false;
    photoUrl = aPhotoUrl;
    wasSet = true;
    return wasSet;
  }

  public int getGame_id() {
    return game_id;
  }

  /* Code from template attribute_GetUnique */
  public static Game getWithGame_id(int aGame_id) {
    return gamesByGame_id.get(aGame_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithGame_id(int aGame_id) {
    return getWithGame_id(aGame_id) != null;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public double getPrice() {
    return price;
  }

  public GameStatus getGameStatus() {
    return gameStatus;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  /* Code from template association_GetMany */
  public Category getCategory(int index) {
    Category aCategory = categories.get(index);
    return aCategory;
  }

  public List<Category> getCategories() {
    List<Category> newCategories = Collections.unmodifiableList(categories);
    return newCategories;
  }

  public int numberOfCategories() {
    int number = categories.size();
    return number;
  }

  public boolean hasCategories() {
    boolean has = categories.size() > 0;
    return has;
  }

  public int indexOfCategory(Category aCategory) {
    int index = categories.indexOf(aCategory);
    return index;
  }

  /* Code from template association_GetMany */
  public Platform getPlatform(int index) {
    Platform aPlatform = platforms.get(index);
    return aPlatform;
  }

  public List<Platform> getPlatforms() {
    List<Platform> newPlatforms = Collections.unmodifiableList(platforms);
    return newPlatforms;
  }

  public void setPlatforms(List<Platform> platforms) {
    this.platforms = platforms;
  }

  public int numberOfPlatforms() {
    int number = platforms.size();
    return number;
  }

  public boolean hasPlatforms() {
    boolean has = platforms.size() > 0;
    return has;
  }

  public int indexOfPlatform(Platform aPlatform) {
    int index = platforms.indexOf(aPlatform);
    return index;
  }

  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfCategories() {
    return 0;
  }

  /* Code from template association_AddUnidirectionalMany */
  public boolean addCategory(Category aCategory) {
    boolean wasAdded = false;
    if (categories.contains(aCategory)) {
      return false;
    }
    categories.add(aCategory);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeCategory(Category aCategory) {
    boolean wasRemoved = false;
    if (categories.contains(aCategory)) {
      categories.remove(aCategory);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  /* Code from template association_AddIndexControlFunctions */
  public boolean addCategoryAt(Category aCategory, int index) {
    boolean wasAdded = false;
    if (addCategory(aCategory)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfCategories()) {
        index = numberOfCategories() - 1;
      }
      categories.remove(aCategory);
      categories.add(index, aCategory);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveCategoryAt(Category aCategory, int index) {
    boolean wasAdded = false;
    if (categories.contains(aCategory)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfCategories()) {
        index = numberOfCategories() - 1;
      }
      categories.remove(aCategory);
      categories.add(index, aCategory);
      wasAdded = true;
    } else {
      wasAdded = addCategoryAt(aCategory, index);
    }
    return wasAdded;
  }

  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfPlatforms() {
    return 0;
  }

  /* Code from template association_AddUnidirectionalMany */
  public boolean addPlatform(Platform aPlatform) {
    boolean wasAdded = false;
    if (platforms.contains(aPlatform)) {
      return false;
    }
    platforms.add(aPlatform);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removePlatform(Platform aPlatform) {
    boolean wasRemoved = false;
    if (platforms.contains(aPlatform)) {
      platforms.remove(aPlatform);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  /* Code from template association_AddIndexControlFunctions */
  public boolean addPlatformAt(Platform aPlatform, int index) {
    boolean wasAdded = false;
    if (addPlatform(aPlatform)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfPlatforms()) {
        index = numberOfPlatforms() - 1;
      }
      platforms.remove(aPlatform);
      platforms.add(index, aPlatform);
      wasAdded = true;
    }
    return wasAdded;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  public boolean addOrMovePlatformAt(Platform aPlatform, int index) {
    boolean wasAdded = false;
    if (platforms.contains(aPlatform)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfPlatforms()) {
        index = numberOfPlatforms() - 1;
      }
      platforms.remove(aPlatform);
      platforms.add(index, aPlatform);
      wasAdded = true;
    } else {
      wasAdded = addPlatformAt(aPlatform, index);
    }
    return wasAdded;
  }

  public void delete() {
    gamesByGame_id.remove(getGame_id());
    categories.clear();
    platforms.clear();
  }

  public String toString() {
    return super.toString() + "[" +
        "game_id" + ":" + getGame_id() + "," +
        "description" + ":" + getDescription() + "," +
        "price" + ":" + getPrice() + "," +
        "stockQuantity" + ":" + getStockQuantity() + "," +
        "photoUrl" + ":" + getPhotoUrl() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "gameStatus" + "="
        + (getGameStatus() != null
            ? !getGameStatus().equals(this) ? getGameStatus().toString().replaceAll("  ", "    ") : "this"
            : "null");
  }

  public static void clearTestEmails(List<Integer> test_integers) {
    for (Integer test_integer : test_integers) {
      gamesByGame_id.remove(test_integer);
    }
  }
}