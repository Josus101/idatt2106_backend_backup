package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * HouseholdMembers model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "householdMembers")
@Getter
@Setter
public class HouseholdMembers {

  @EmbeddedId
  private HouseholdMembersId id;

  @ManyToOne
  @MapsId("user")
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @MapsId("household")
  @JoinColumn(name = "household_id")
  private Household household;

  @Column(name = "is_admin", nullable = false)
  private boolean isAdmin;

  @Column(name = "is_primary", nullable = false)
  private boolean isPrimary;

  /**
   * Blank constructor for the HouseholdMembers model
   */
  public HouseholdMembers() {}


  /**
   * Constructor of the HouseholdMembers model
   * @param user the member of the household
   * @param household the household
   * @param isAdmin flag to set a member as the household creator/admin
   */
  public HouseholdMembers(User user, Household household, boolean isAdmin, boolean isPrimary) {
    if (user == null || household == null) {
      throw new IllegalArgumentException("User or Household cannot be null");
    }
    this.user = user;
    this.household = household;
    this.id = new HouseholdMembersId(user.getId(), household.getId());
    this.isAdmin = isAdmin;
    this.isPrimary = isPrimary;
  }

  /**
   * ToString method for the HouseholdMembers model
   */
  @Override
  public String toString() {
    return "HouseholdMembers{" +
            "user=" + user +
            ", household=" + household +
            ", isAdmin=" + isAdmin +
            '}';
  }
}
