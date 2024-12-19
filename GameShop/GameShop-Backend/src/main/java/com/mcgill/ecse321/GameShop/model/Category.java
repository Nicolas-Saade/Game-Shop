/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import java.util.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

// line 84 "../../../../../../model.ump"
// line 240 "../../../../../../model.ump"
@Entity
public class Category {

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, Category> categorysByCategory_id = new HashMap<Integer, Category>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Category Attributes
  @Id
  @GeneratedValue
  private int category_id;
  private String categoryName;

  // Category Associations
  @ManyToOne(cascade = CascadeType.ALL)
  // @JoinColumn(name = "email", nullable = true)
  private Manager manager;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Category(String aCategoryName, Manager aManager) {
    categoryName = aCategoryName;
    if (!setManager(aManager)) {
      throw new RuntimeException(
          "Unable to create Category due to aManager. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  protected Category() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setCategory_id(int aCategory_id) {
    boolean wasSet = false;
    Integer anOldCategory_id = getCategory_id();
    if (anOldCategory_id != null && anOldCategory_id.equals(aCategory_id)) {
      return true;
    }
    if (hasWithCategory_id(aCategory_id)) {
      return wasSet;
    }
    category_id = aCategory_id;
    wasSet = true;
    if (anOldCategory_id != null) {
      categorysByCategory_id.remove(anOldCategory_id);
    }
    categorysByCategory_id.put(aCategory_id, this);
    return wasSet;
  }

  public boolean setCategoryName(String aCategoryName) {
    boolean wasSet = false;
    categoryName = aCategoryName;
    wasSet = true;
    return wasSet;
  }

  public int getCategory_id() {
    return category_id;
  }

  /* Code from template attribute_GetUnique */
  public static Category getWithCategory_id(int aCategory_id) {
    return categorysByCategory_id.get(aCategory_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithCategory_id(int aCategory_id) {
    return getWithCategory_id(aCategory_id) != null;
  }

  public String getCategoryName() {
    return categoryName;
  }

  /* Code from template association_GetOne */
  public Manager getManager() {
    return manager;
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
    categorysByCategory_id.remove(getCategory_id());
    manager = null;
  }
  public void removeManager() {
    this.manager = null;
  }

  public String toString() {
    return super.toString() + "[" +
        "category_id" + ":" + getCategory_id() + "," +
        "categoryName" + ":" + getCategoryName() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "manager = "
        + (getManager() != null ? Integer.toHexString(System.identityHashCode(getManager())) : "null");
  }

}