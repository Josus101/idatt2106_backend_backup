package org.ntnu.idatt2106.backend.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HouseholdMembersId implements Serializable {

  private int user;
  private int household;

  public HouseholdMembersId() {}

  public HouseholdMembersId(int user, int household) {
    this.user = user;
    this.household = household;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HouseholdMembersId that)) return false;
    return user == that.user && household == that.household;
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, household);
  }

  // Getters and Setters
}
