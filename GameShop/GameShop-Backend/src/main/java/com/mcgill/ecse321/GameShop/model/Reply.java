/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.34.0.7242.6b8819789 modeling language!*/

package com.mcgill.ecse321.GameShop.model;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.sql.Date;

// line 60 "../../../../../../model.ump"
// line 227 "../../../../../../model.ump"
@Entity
public class Reply {

  // ------------------------
  // ENUMERATIONS
  // ------------------------

  public enum ReviewRating {
    Like, Dislike
  }

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, Reply> replysByReply_id = new HashMap<Integer, Reply>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Reply Attributes
  @Id
  @GeneratedValue
  private int reply_id;
  private Date replyDate;
  private String description;
  private ReviewRating reviewRating;

  // Reply Associations
  @ManyToOne
  @JoinColumn(name = "review_id")
  private Review review;

  @ManyToOne
  private Manager manager;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Reply(Date aReplyDate, String aDescription, Review aReview, Manager aManager) {
    replyDate = aReplyDate;
    description = aDescription;

    boolean didAddReview = setReview(aReview);
    if (!didAddReview) {
      throw new RuntimeException(
          "Unable to create reply due to review. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    if (!setManager(aManager)) {
      throw new RuntimeException(
          "Unable to create Reply due to aManager. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  protected Reply() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setReply_id(int aReply_id) {
    boolean wasSet = false;
    Integer anOldReply_id = getReply_id();
    if (anOldReply_id != null && anOldReply_id.equals(aReply_id)) {
      return true;
    }
    if (hasWithReply_id(aReply_id)) {
      return wasSet;
    }
    reply_id = aReply_id;
    wasSet = true;
    if (anOldReply_id != null) {
      replysByReply_id.remove(anOldReply_id);
    }
    replysByReply_id.put(aReply_id, this);
    return wasSet;
  }

  public boolean setReplyDate(Date aReplyDate) {
    boolean wasSet = false;
    replyDate = aReplyDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setDescription(String aDescription) {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public boolean setReviewRating(ReviewRating aReviewRating) {
    boolean wasSet = false;
    reviewRating = aReviewRating;
    wasSet = true;
    return wasSet;
  }

  public int getReply_id() {
    return reply_id;
  }

  /* Code from template attribute_GetUnique */
  public static Reply getWithReply_id(int aReply_id) {
    return replysByReply_id.get(aReply_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithReply_id(int aReply_id) {
    return getWithReply_id(aReply_id) != null;
  }

  public Date getReplyDate() {
    return replyDate;
  }

  public String getDescription() {
    return description;
  }

  public ReviewRating getReviewRating() {
    return reviewRating;
  }

  /* Code from template association_GetOne */
  public Review getReview() {
    return review;
  }

  /* Code from template association_GetOne */
  public Manager getManager() {
    return manager;
  }

  /* Code from template association_SetOneToMany */
  public boolean setReview(Review aReview) {
    boolean wasSet = false;
    if (aReview == null) {
      return wasSet;
    }
    review = aReview;
    wasSet = true;
    return wasSet;
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
    replysByReply_id.remove(getReply_id());
    this.review = null;
    manager = null;
  }

  public String toString() {
    return super.toString() + "[" +
        "reply_id" + ":" + getReply_id() + "," +
        "description" + ":" + getDescription() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "replyDate" + "="
        + (getReplyDate() != null
            ? !getReplyDate().equals(this) ? getReplyDate().toString().replaceAll("  ", "    ") : "this"
            : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "reviewRating" + "="
        + (getReviewRating() != null
            ? !getReviewRating().equals(this) ? getReviewRating().toString().replaceAll("  ", "    ") : "this"
            : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "review = " + (getReview() != null ? Integer.toHexString(System.identityHashCode(getReview())) : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "manager = "
        + (getManager() != null ? Integer.toHexString(System.identityHashCode(getManager())) : "null");
  }
}