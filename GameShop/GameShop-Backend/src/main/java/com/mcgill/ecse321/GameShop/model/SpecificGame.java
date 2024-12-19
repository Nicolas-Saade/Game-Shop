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

// line 94 "../../../../../../model.ump"
// line 245 "../../../../../../model.ump"
@Entity
public class SpecificGame {

  // ------------------------
  // ENUMERATIONS
  // ------------------------

  public enum ItemStatus {
    Confirmed, Returned
  }

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, SpecificGame> specificgamesBySpecificGame_id = new HashMap<Integer, SpecificGame>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // SpecificGame Attributes
  @Id
  @GeneratedValue
  private int specificGame_id;
  private ItemStatus itemStatus;

  // SpecificGame Associations
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "specificGame_jt", joinColumns = @JoinColumn(name = "specificGame_id"), inverseJoinColumns = @JoinColumn(name = "order_id"))
  private List<Order> order;
  @ManyToOne
  private Game games;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public SpecificGame(Game aGames) {
    itemStatus = ItemStatus.Confirmed;

    order = new ArrayList<Order>();
    if (!setGames(aGames)) {
      throw new RuntimeException(
          "Unable to create SpecificGame due to aGames. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  protected SpecificGame() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setSpecificGame_id(int aSpecificGame_id) {
    boolean wasSet = false;
    Integer anOldSpecificGame_id = getSpecificGame_id();
    if (anOldSpecificGame_id != null && anOldSpecificGame_id.equals(aSpecificGame_id)) {
      return true;
    }
    if (hasWithSpecificGame_id(aSpecificGame_id)) {
      return wasSet;
    }
    specificGame_id = aSpecificGame_id;
    wasSet = true;
    if (anOldSpecificGame_id != null) {
      specificgamesBySpecificGame_id.remove(anOldSpecificGame_id);
    }
    specificgamesBySpecificGame_id.put(aSpecificGame_id, this);
    return wasSet;
  }

  public boolean setItemStatus(ItemStatus aItemStatus) {
    boolean wasSet = false;
    itemStatus = aItemStatus;
    wasSet = true;
    return wasSet;
  }

  public int getSpecificGame_id() {
    return specificGame_id;
  }

  /* Code from template attribute_GetUnique */
  public static SpecificGame getWithSpecificGame_id(int aSpecificGame_id) {
    return specificgamesBySpecificGame_id.get(aSpecificGame_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithSpecificGame_id(int aSpecificGame_id) {
    return getWithSpecificGame_id(aSpecificGame_id) != null;
  }

  public ItemStatus getItemStatus() {
    return itemStatus;
  }

  /* Code from template association_GetMany */
  public Order getOrder(int index) {
    Order aOrder = order.get(index);
    return aOrder;
  }

  public List<Order> getOrder() {
    List<Order> newOrder = Collections.unmodifiableList(order);
    return newOrder;
  }

  public int numberOfOrder() {
    int number = order.size();
    return number;
  }

  public boolean hasOrder() {
    boolean has = order.size() > 0;
    return has;
  }

  public int indexOfOrder(Order aOrder) {
    int index = order.indexOf(aOrder);
    return index;
  }

  /* Code from template association_GetOne */
  public Game getGames() {
    return games;
  }

  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfOrder() {
    return 0;
  }

  /* Code from template association_AddUnidirectionalMany */
  public boolean addOrder(Order aOrder) {
    boolean wasAdded = false;
    if (order.contains(aOrder)) {
      return false;
    }
    order.add(aOrder);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeOrder(Order aOrder) {
    boolean wasRemoved = false;
    if (order.contains(aOrder)) {
      order.remove(aOrder);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  /* Code from template association_AddIndexControlFunctions */
  public boolean addOrderAt(Order aOrder, int index) {
    boolean wasAdded = false;
    if (addOrder(aOrder)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfOrder()) {
        index = numberOfOrder() - 1;
      }
      order.remove(aOrder);
      order.add(index, aOrder);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveOrderAt(Order aOrder, int index) {
    boolean wasAdded = false;
    if (order.contains(aOrder)) {
      if (index < 0) {
        index = 0;
      }
      if (index > numberOfOrder()) {
        index = numberOfOrder() - 1;
      }
      order.remove(aOrder);
      order.add(index, aOrder);
      wasAdded = true;
    } else {
      wasAdded = addOrderAt(aOrder, index);
    }
    return wasAdded;
  }

  /* Code from template association_SetUnidirectionalOne */
  public boolean setGames(Game aNewGames) {
    boolean wasSet = false;
    if (aNewGames != null) {
      games = aNewGames;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete() {
    specificgamesBySpecificGame_id.remove(getSpecificGame_id());
    order.clear();
    games = null;
  }

  public String toString() {
    return super.toString() + "[" +
        "specificGame_id" + ":" + getSpecificGame_id() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "itemStatus" + "="
        + (getItemStatus() != null
            ? !getItemStatus().equals(this) ? getItemStatus().toString().replaceAll("  ", "    ") : "this"
            : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "games = " + (getGames() != null ? Integer.toHexString(System.identityHashCode(getGames())) : "null");
  }
}