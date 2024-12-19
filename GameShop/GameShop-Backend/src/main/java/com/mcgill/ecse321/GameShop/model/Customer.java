/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

// line 19 "../../../../../../model.ump"
// line 210 "../../../../../../model.ump"
@Entity
public class Customer extends Account {

  // ------------------------
  // MEMBER VARIABLESf
  // ------------------------

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Cart cart;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Customer(String aEmail, String aUsername, String aPassword, String aPhoneNumber, String aAddress, Cart aCart) {
    super(aEmail, aUsername, aPassword, aPhoneNumber, aAddress);
    // wishList = new ArrayList<WishList>();
    if (!setCart(aCart)) {
      throw new RuntimeException(
          "Unable to create Customer due to aCart. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  protected Customer() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  /* Code from template association_GetOne */
  public Cart getCart() {
    if (cart != null) {
      return cart;
    }
    return null;
  }

  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfWishList() {
    return 0;
  }

  /* Code from template association_AddManyToOne */
  public WishList addWishList(String aTitle) {
    return new WishList(aTitle, this);
  }

  /* Code from template association_SetUnidirectionalOne */
  public boolean setCart(Cart aNewCart) {
    boolean wasSet = false;
    if (aNewCart != null) {
      cart = aNewCart;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete() {
    cart = null;
    super.delete();
  }
}