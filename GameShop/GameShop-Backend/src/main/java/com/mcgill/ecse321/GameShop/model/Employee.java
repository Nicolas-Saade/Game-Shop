/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.7523.c616a4dce modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import jakarta.persistence.Entity;

// line 26 "../../../../../../model.ump"
// line 190 "../../../../../../model.ump"
@Entity
public class Employee extends Staff {

  // ------------------------
  // ENUMERATIONS
  // ------------------------

  public enum EmployeeStatus {
    Active, Archived
  }

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Employee Attributes
  private EmployeeStatus employeeStatus;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Employee(String aEmail, String aUsername, String aPassword, String aPhoneNumber, String aAddress) {
    super(aEmail, aUsername, aPassword, aPhoneNumber, aAddress);
    employeeStatus = EmployeeStatus.Active;
  }

  protected Employee() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setEmployeeStatus(EmployeeStatus aEmployeeStatus) {
    boolean wasSet = false;
    employeeStatus = aEmployeeStatus;
    wasSet = true;
    return wasSet;
  }

  public EmployeeStatus getEmployeeStatus() {
    return employeeStatus;
  }
}