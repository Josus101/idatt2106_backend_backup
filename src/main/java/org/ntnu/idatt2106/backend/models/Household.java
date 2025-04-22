package org.ntnu.idatt2106.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "household")
@Getter
@Setter
public class Household {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column
  private double latitude;

  @Column
  private double longitude;

  @OneToMany(mappedBy = "household")
  private List<HouseholdMembers> members;

  @ManyToMany
  @JoinTable(
          name = "inventory",
          joinColumns = @JoinColumn(name = "household_id"),
          inverseJoinColumns = @JoinColumn(name = "item_id")
  )
  private List<Item> inventory;

  public Household() {}

  public Household(
          int id,
          String name,
          double latitude,
          double longitude,
          List<HouseholdMembers> members,
          List<Item> inventory) {
    this.id = id;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.members = members;
    this.inventory = inventory;
  }
}
