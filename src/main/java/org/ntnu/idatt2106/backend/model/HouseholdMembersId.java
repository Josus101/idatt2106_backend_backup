package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Concat key of the HouseholdMembers model
 * @Author Jonas Reiher
 * @since 0.1
 */
@Embeddable
@Getter
@Setter
public class HouseholdMembersId implements Serializable {

  private int user;
  private int household;


  /**
   * Blank constructor for the concat key of the HouseholdMembers model
   */
  public HouseholdMembersId() {}


  /**
   * Constructor for the concat key of the HouseholdMembers model
   * @param user the member of the household
   * @param household the household
   */
  public HouseholdMembersId(int user, int household) {
    this.user = user;
    this.household = household;
  }

  /**
   * Method to validate if the concat key o is equals to {@code this} concat key
   * @param o the other concat key
   * @return true if the keys are equals, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HouseholdMembersId that)) return false;
    return user == that.user && household == that.household;
  }

  /**
   * Hashing method
   * @return the combined hashed value of the user and household fields
   */
  @Override
  public int hashCode() {
    return Objects.hash(user, household);
  }

}
