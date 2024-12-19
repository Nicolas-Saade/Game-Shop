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

// line 36 "../../../../../../model.ump"
// line 265 "../../../../../../model.ump"
@Entity
public class WishList {

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, WishList> wishlistsByWishList_id = new HashMap<Integer, WishList>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // WishList Attributes
  @Id
  @GeneratedValue
  private int wishList_id;
  private String title;

  // WishList Associations
  @ManyToOne
  @JoinColumn(name = "email")
  private Customer customer;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "wishList_jt", joinColumns = @JoinColumn(name = "wishList_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
  private List<Game> games;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public WishList(String aTitle, Customer aCustomer) {
    title = aTitle;

    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer) {
      throw new RuntimeException(
          "Unable to create wishList due to customer. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    games = new ArrayList<Game>();
  }

  protected WishList() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setWishList_id(int aWishList_id) {
    boolean wasSet = false;
    Integer anOldWishList_id = getWishList_id();
    if (anOldWishList_id != null && anOldWishList_id.equals(aWishList_id)) {
      return true;
    }
    if (hasWithWishList_id(aWishList_id)) {
      return wasSet;
    }
    wishList_id = aWishList_id;
    wasSet = true;
    if (anOldWishList_id != null) {
      wishlistsByWishList_id.remove(anOldWishList_id);
    }
    wishlistsByWishList_id.put(aWishList_id, this);
    return wasSet;
  }
  public void setGames(List<Game> games) {
    this.games = games;
}

  public boolean setTitle(String aTitle) {
    boolean wasSet = false;
    title = aTitle;
    wasSet = true;
    return wasSet;
  }

  public int getWishList_id() {
    return wishList_id;
  }

  /* Code from template attribute_GetUnique */
  public static WishList getWithWishList_id(int aWishList_id) {
    return wishlistsByWishList_id.get(aWishList_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithWishList_id(int aWishList_id) {
    return getWithWishList_id(aWishList_id) != null;
  }

  public String getTitle() {
    return title;
  }

  /* Code from template association_GetOne */
  public Customer getCustomer() {
    return customer;
  }

  /* Code from template association_GetMany */
  public Game getGame(int index) {
    Game aGame = games.get(index);
    return aGame;
  }

  public List<Game> getGames() {
    List<Game> newGames = Collections.unmodifiableList(games);
    return newGames;
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

  /* Code from template association_SetOneToMany */
  public boolean setCustomer(Customer aCustomer) {
    boolean wasSet = false;
    if (aCustomer == null) {
      return wasSet;
    }
    customer = aCustomer;
    wasSet = true;
    return wasSet;
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

  public void delete() {
    wishlistsByWishList_id.remove(getWishList_id());
    this.customer = null;
    games.clear();
  }

  public String toString() {
    return super.toString() + "[" +
        "wishList_id" + ":" + getWishList_id() + "," +
        "title" + ":" + getTitle() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "customer = "
        + (getCustomer() != null ? Integer.toHexString(System.identityHashCode(getCustomer())) : "null");
  }
}