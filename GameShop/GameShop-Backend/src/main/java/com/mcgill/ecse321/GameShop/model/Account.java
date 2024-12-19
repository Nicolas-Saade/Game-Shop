/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

// line 4 "../../../../../../model.ump"
// line 200 "../../../../../../model.ump"
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Account {

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<String, Account> accountsByEmail = new HashMap<String, Account>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Account Attributes
  @Id
  private String email;
  private String username;
  private String password;
  private String phoneNumber;
  private String address;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Account(String aEmail, String aUsername, String aPassword, String aPhoneNumber, String aAddress) {
    username = aUsername;
    password = aPassword;
    phoneNumber = aPhoneNumber;
    address = aAddress;
    if (!setEmail(aEmail)) {
      throw new RuntimeException(
          "Cannot create due to duplicate email. See https://manual.umple.org?RE003ViolationofUniqueness.html");
    }
  }

  protected Account() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setEmail(String aEmail) {
    boolean wasSet = false;
    String anOldEmail = getEmail();
    if (anOldEmail != null && anOldEmail.equals(aEmail)) {
      return true;
    }
    if (hasWithEmail(aEmail)) {
      return wasSet;
    }
    email = aEmail;
    wasSet = true;
    if (anOldEmail != null) {
      accountsByEmail.remove(anOldEmail);
    }
    accountsByEmail.put(aEmail, this);
    return wasSet;
  }

  public boolean setUsername(String aUsername) {
    boolean wasSet = false;
    username = aUsername;
    wasSet = true;
    return wasSet;
  }

  public boolean setPassword(String aPassword) {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public boolean setPhoneNumber(String aPhoneNumber) {
    boolean wasSet = false;
    phoneNumber = aPhoneNumber;
    wasSet = true;
    return wasSet;
  }

  public boolean setAddress(String aAddress) {
    boolean wasSet = false;
    address = aAddress;
    wasSet = true;
    return wasSet;
  }

  public String getEmail() {
    return email;
  }

  /* Code from template attribute_GetUnique */
  public static Account getWithEmail(String aEmail) {
    return accountsByEmail.get(aEmail);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithEmail(String aEmail) {
    return getWithEmail(aEmail) != null;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public void delete() {
    accountsByEmail.remove(getEmail());
  }

  public String toString() {
    return super.toString() + "[" +
        "email" + ":" + getEmail() + "," +
        "username" + ":" + getUsername() + "," +
        "password" + ":" + getPassword() + "," +
        "phoneNumber" + ":" + getPhoneNumber() + "," +
        "address" + ":" + getAddress() + "]";
  }

  public static void clearTestEmails(List<String> test_emails) {
    for (String test_email : test_emails) {
      accountsByEmail.remove(test_email);
    }
  }
}