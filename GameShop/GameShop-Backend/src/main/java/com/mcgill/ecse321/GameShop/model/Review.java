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
import java.time.LocalDate;

// line 49 "../../../../../../model.ump"
// line 221 "../../../../../../model.ump"
@Entity
public class Review {

  // ------------------------
  // ENUMERATIONS
  // ------------------------

  public enum GameRating {
    One, Two, Three, Four, Five
  }

  // ------------------------
  // STATIC VARIABLES
  // ------------------------

  private static Map<Integer, Review> reviewsByReview_id = new HashMap<Integer, Review>();

  // ------------------------
  // MEMBER VARIABLES
  // ------------------------

  // Review Attributes
  @Id
  @GeneratedValue
  private int review_id;
  private LocalDate reviewDate;
  private String description;
  private int rating;
  private GameRating gameRating;

  @ManyToOne
  @JoinColumn(name = "game_id")
  private Game game;

  @ManyToOne
  private Customer customer;

  // ------------------------
  // CONSTRUCTOR
  // ------------------------

  public Review(LocalDate aReviewDate, String aDescription, int aRating, GameRating aGameRating, Game aGame,
      Customer aCustomer) {
    reviewDate = aReviewDate;
    description = aDescription;
    rating = aRating;
    gameRating = aGameRating;

    boolean didAddGame = setGame(aGame);
    if (!didAddGame) {
      throw new RuntimeException(
          "Unable to create review due to game. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    if (!setCustomer(aCustomer)) {
      throw new RuntimeException(
          "Unable to create Review due to aCustomer. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  protected Review() {
  }

  // ------------------------
  // INTERFACE
  // ------------------------

  public boolean setReview_id(int aReview_id) {
    boolean wasSet = false;
    Integer anOldReview_id = getReview_id();
    if (anOldReview_id != null && anOldReview_id.equals(aReview_id)) {
      return true;
    }
    if (hasWithReview_id(aReview_id)) {
      return wasSet;
    }
    review_id = aReview_id;
    wasSet = true;
    if (anOldReview_id != null) {
      reviewsByReview_id.remove(anOldReview_id);
    }
    reviewsByReview_id.put(aReview_id, this);
    return wasSet;
  }

  public boolean setReviewDate(LocalDate aReviewDate) {
    boolean wasSet = false;
    reviewDate = aReviewDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setDescription(String aDescription) {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public boolean setRating(int aRating) {
    boolean wasSet = false;
    rating = aRating;
    wasSet = true;
    return wasSet;
  }

  public boolean setGameRating(GameRating aGameRating) {
    boolean wasSet = false;
    gameRating = aGameRating;
    wasSet = true;
    return wasSet;
  }

  public int getReview_id() {
    return review_id;
  }

  /* Code from template attribute_GetUnique */
  public static Review getWithReview_id(int aReview_id) {
    return reviewsByReview_id.get(aReview_id);
  }

  /* Code from template attribute_HasUnique */
  public static boolean hasWithReview_id(int aReview_id) {
    return getWithReview_id(aReview_id) != null;
  }

  public LocalDate getReviewDate() {
    return reviewDate;
  }

  public String getDescription() {
    return description;
  }

  public int getRating() {
    return rating;
  }

  public GameRating getGameRating() {
    return gameRating;
  }

  /* Code from template association_GetOne */
  public Game getGame() {
    return game;
  }

  /* Code from template association_GetOne */
  public Customer getCustomer() {
    return customer;
  }

  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfReply() {
    return 0;
  }

  /* Code from template association_AddManyToOne */
  public Reply addReply(int aReply_id, Date aReplyDate, String aDescription, Manager aManager) {
    return new Reply(aReplyDate, aDescription, this, aManager);
  }

  /* Code from template association_SetOneToMany */
  public boolean setGame(Game aGame) {
    boolean wasSet = false;
    if (aGame == null) {
      return wasSet;
    }
    game = aGame;
    wasSet = true;
    return wasSet;
  }

  /* Code from template association_SetUnidirectionalOne */
  public boolean setCustomer(Customer aNewCustomer) {
    boolean wasSet = false;
    if (aNewCustomer != null) {
      customer = aNewCustomer;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete() {
    reviewsByReview_id.remove(getReview_id());
    this.game = null;
    customer = null;
  }

  public String toString() {
    return super.toString() + "[" +
        "review_id" + ":" + getReview_id() + "," +
        "description" + ":" + getDescription() + "," +
        "rating" + ":" + getRating() + "]" + System.getProperties().getProperty("line.separator") +
        "  " + "reviewDate" + "="
        + (getReviewDate() != null
            ? !getReviewDate().equals(this) ? getReviewDate().toString().replaceAll("  ", "    ") : "this"
            : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "gameRating" + "="
        + (getGameRating() != null
            ? !getGameRating().equals(this) ? getGameRating().toString().replaceAll("  ", "    ") : "this"
            : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "game = " + (getGame() != null ? Integer.toHexString(System.identityHashCode(getGame())) : "null")
        + System.getProperties().getProperty("line.separator") +
        "  " + "customer = "
        + (getCustomer() != null ? Integer.toHexString(System.identityHashCode(getCustomer())) : "null");
  }
}