package org.ntnu.idatt2106.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

  public HouseholdMembers() {}

  public HouseholdMembers(User user, Household household, boolean isAdmin) {
    this.user = user;
    this.household = household;
    this.id = new HouseholdMembersId(user.getId(), household.getId());
    this.isAdmin = isAdmin;
  }
}
