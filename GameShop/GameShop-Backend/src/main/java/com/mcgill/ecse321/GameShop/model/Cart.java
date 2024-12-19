/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

// line 108 "../../../../../../model.ump"
// line 250 "../../../../../../model.ump"
@Entity
public class Cart {

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, Cart> cartsByCart_id = new HashMap<Integer, Cart>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Cart Attributes
  @Id
  @GeneratedValue
  private int cart_id;

  // Cart Associations
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "cart_items_jt", joinColumns = @JoinColumn(name = "cart_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
  private List<Game> games;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Cart() {

    games = new ArrayList<Game>();
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setCart_id(int aCart_id) {
    boolean wasSet = false;
    Integer anOldCart_id = getCart_id();
    if (anOldCart_id != null && anOldCart_id.equals(aCart_id)) {
      return true;
    }
    if (hasWithCart_id(aCart_id)) {
      return wasSet;
    }
    cart_id = aCart_id;
    wasSet = true;
    if (anOldCart_id != null) {
      cartsByCart_id.remove(anOldCart_id);
    }
    cartsByCart_id.put(aCart_id, this);
    return wasSet;
  }

  public int getCart_id() {
    return cart_id;
  }

  /* Code from template attribute_GetUnique */
  public static Cart getWithCart_id(int aCart_id) {
    return cartsByCart_id.get(aCart_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithCart_id(int aCart_id) {
    return getWithCart_id(aCart_id) != null;
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
    cartsByCart_id.remove(getCart_id());
    games.clear();
  }

  public String toString() {
    return super.toString() + "[" +
        "cart_id" + ":" + getCart_id() + "]";
  }
}