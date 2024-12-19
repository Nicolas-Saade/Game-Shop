/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

// line 89 "../../../../../../model.ump"
// line 255 "../../../../../../model.ump"
@Entity
public class Platform {

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, Platform> platformsByPlatform_id = new HashMap<Integer, Platform>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Platform Attributes
  @Id
  @GeneratedValue
  private int platform_id;
  private String platformName;

  // Platform Associations
  @ManyToOne
  private Manager manager;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Platform(String aPlatformName, Manager aManager) {
    platformName = aPlatformName;
    if (!setManager(aManager)) {
      throw new RuntimeException(
          "Unable to create Platform due to aManager. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  protected Platform() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setPlatform_id(int aPlatform_id) {
    boolean wasSet = false;
    Integer anOldPlatform_id = getPlatform_id();
    if (anOldPlatform_id != null && anOldPlatform_id.equals(aPlatform_id)) {
      return true;
    }
    if (hasWithPlatform_id(aPlatform_id)) {
      return wasSet;
    }
    platform_id = aPlatform_id;
    wasSet = true;
    if (anOldPlatform_id != null) {
      platformsByPlatform_id.remove(anOldPlatform_id);
    }
    platformsByPlatform_id.put(aPlatform_id, this);
    return wasSet;
  }

  public boolean setPlatformName(String aPlatformName) {
    boolean wasSet = false;
    platformName = aPlatformName;
    wasSet = true;
    return wasSet;
  }

  public int getPlatform_id() {
    return platform_id;
  }

  /* Code from template attribute_GetUnique */
  public static Platform getWithPlatform_id(int aPlatform_id) {
    return platformsByPlatform_id.get(aPlatform_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithPlatform_id(int aPlatform_id) {
    return getWithPlatform_id(aPlatform_id) != null;
  }

  public String getPlatformName() {
    return platformName;
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
    platformsByPlatform_id.remove(getPlatform_id());
    manager = null;
  }

  public String toString() {
    return super.toString() + "[" +
        "platform_id" + ":" + getPlatform_id() + "," +
        "platformName" + ":" + getPlatformName() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "manager = "
        + (getManager() != null ? Integer.toHexString(System.identityHashCode(getManager())) : "null");
  }

  public void removeManager() {
    this.manager = null;
  }
  
}