package org.ntnu.idatt2106.backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "householdMembers")
public class HouseholdMembers {

  @Id
  private int id;

  @OneToOne
  @JoinColumn(name = "user")
  private User user;

  @OneToOne
  @JoinColumn(name = "household")
  private Household household;

  @Column(nullable = false)
  private boolean isAdmin;
}
