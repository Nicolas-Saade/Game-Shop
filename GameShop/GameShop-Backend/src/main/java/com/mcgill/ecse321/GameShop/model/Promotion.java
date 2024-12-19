/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

// line 100 "../../../../../../model.ump"
// line 250 "../../../../../../model.ump"
@Entity
public class Promotion {

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, Promotion> promotionsByPromotion_id = new HashMap<Integer, Promotion>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Promotion Attributes
  @Id
  @GeneratedValue
  private int promotion_id;
  private String description;
  private int discountRate;
  private LocalDate startLocalDate;
  private LocalDate endLocalDate;

  // Promotion Associations
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "promotion_jt", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
  private List<Game> games;
  @ManyToOne
  private Manager manager;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Promotion(String aDescription, int aDiscountRate, LocalDate aStartLocalDate, LocalDate aEndLocalDate, Manager aManager) {
    description = aDescription;
    discountRate = aDiscountRate;
    startLocalDate = aStartLocalDate;
    endLocalDate = aEndLocalDate;
    games = new ArrayList<Game>();
    if (!setManager(aManager)) {
      throw new RuntimeException(
          "Unable to create Promotion due to aManager. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  protected Promotion() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setPromotion_id(int aPromotion_id) {
    boolean wasSet = false;
    Integer anOldPromotion_id = getPromotion_id();
    if (anOldPromotion_id != null && anOldPromotion_id.equals(aPromotion_id)) {
      return true;
    }
    if (hasWithPromotion_id(aPromotion_id)) {
      return wasSet;
    }
    promotion_id = aPromotion_id;
    wasSet = true;
    if (anOldPromotion_id != null) {
      promotionsByPromotion_id.remove(anOldPromotion_id);
    }
    promotionsByPromotion_id.put(aPromotion_id, this);
    return wasSet;
  }

  public boolean setDescription(String aDescription) {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public boolean setDiscountRate(int aDiscountRate) {
    boolean wasSet = false;
    discountRate = aDiscountRate;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartLocalDate(LocalDate aStartLocalDate) {
    boolean wasSet = false;
    startLocalDate = aStartLocalDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setEndLocalDate(LocalDate aEndLocalDate) {
    boolean wasSet = false;
    endLocalDate = aEndLocalDate;
    wasSet = true;
    return wasSet;
  }

  public int getPromotion_id() {
    return promotion_id;
  }

  /* Code from template attribute_GetUnique */
  public static Promotion getWithPromotion_id(int aPromotion_id) {
    return promotionsByPromotion_id.get(aPromotion_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithPromotion_id(int aPromotion_id) {
    return getWithPromotion_id(aPromotion_id) != null;
  }

  public String getDescription() {
    return description;
  }

  public int getDiscountRate() {
    return discountRate;
  }

  public LocalDate getStartLocalDate() {
    return startLocalDate;
  }

  public LocalDate getEndLocalDate() {
    return endLocalDate;
  }

  /* Code from template association_GetMany */
  public Game getGame(int index) {
    Game aGame = games.get(index);
    return aGame;
  }

  public List<Game> getGames() {
    return this.games;
  }

  public int numberOfGames() {
    int number = games.size();
    return number;
  }

  public boolean hasGames() {
    boolean has = games.size() > 0;
    return has;
  }

  public int indexOfGame(Game aGame) {
    int index = games.indexOf(aGame);
    return index;
  }

  /* Code from template association_GetOne */
  public Manager getManager() {
    return manager;
  }

  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfGames() {
    return 0;
  }

  /* Code from template association_AddUnidirectionalMany */
  public boolean addGame(Game aGame) {
    boolean wasAdded = false;
    if (games.contains(aGame)) {
      return false;
    }
    games.add(aGame);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeGame(Game aGame) {
    boolean wasRemoved = false;
    if (games.contains(aGame)) {
      games.remove(aGame);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  /* Code from template association_AddIndexControlFunctions */
  public boolean addGameAt(Game aGame, int index) {
    boolean wasAdded = false;
    if (addGame(aGame)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfGames()) {
        index = numberOfGames() - 1;
      }
      games.remove(aGame);
      games.add(index, aGame);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveGameAt(Game aGame, int index) {
    boolean wasAdded = false;
    if (games.contains(aGame)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfGames()) {
        index = numberOfGames() - 1;
      }
      games.remove(aGame);
      games.add(index, aGame);
      wasAdded = true;
    } else {
      wasAdded = addGameAt(aGame, index);
    }
    return wasAdded;
  }

  /* Code from template association_SetUnidirectionalOne */
  public boolean setManager(Manager aNewManager) {
    boolean wasSet = false;
    if (aNewManager != null) {
      manager = aNewManager;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete() {
    promotionsByPromotion_id.remove(getPromotion_id());
    games.clear();
    manager = null;
  }

  public String toString() {
    return super.toString() + "[" +
        "promotion_id" + ":" + getPromotion_id() + "," +
        "description" + ":" + getDescription() + "," +
        "discountRate" + ":" + getDiscountRate() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "startLocalDate" + "="
        + (getStartLocalDate() != null
            ? !getStartLocalDate().equals(this) ? getStartLocalDate().toString().replaceAll("  ", "    ") : "this"
            : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "endLocalDate" + "="
        + (getEndLocalDate() != null ? !getEndLocalDate().equals(this) ? getEndLocalDate().toString().replaceAll("  ", "    ") : "this"
            : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "manager = "
        + (getManager() != null ? Integer.toHexString(System.identityHashCode(getManager())) : "null");
  }

  public void setGames(List<Game> games2) {
    this.games = games2;
  }
}
