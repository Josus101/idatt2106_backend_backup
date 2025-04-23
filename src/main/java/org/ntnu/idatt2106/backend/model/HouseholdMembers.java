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
  @JoinColumn(name = "user")
  private User user;

  @ManyToOne
  @MapsId("household")
  @JoinColumn(name = "household")
  private Household household;

  private boolean isAdmin;

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
  public HouseholdMembers(User user, Household household, boolean isAdmin) {
    this.user = user;
    this.household = household;
    this.id = new HouseholdMembersId(user.getId(), household.getId());
    this.isAdmin = isAdmin;
  }
}
