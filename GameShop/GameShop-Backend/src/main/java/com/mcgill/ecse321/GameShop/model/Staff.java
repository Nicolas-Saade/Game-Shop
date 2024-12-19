/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

// line 14 "../../../../../../model.ump"
// line 205 "../../../../../../model.ump"
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Staff extends Account {

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Staff(String aEmail, String aUsername, String aPassword, String aPhoneNumber, String aAddress) {
    super(aEmail, aUsername, aPassword, aPhoneNumber, aAddress);
  }

  protected Staff() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public void delete() {
    super.delete();
  }
}