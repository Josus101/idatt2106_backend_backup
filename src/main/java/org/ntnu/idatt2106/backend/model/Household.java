package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Household model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
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

  @OneToMany(mappedBy = "household", orphanRemoval = true)
  private List<HouseholdMembers> members = new ArrayList<>();

  @ManyToMany
  @JoinTable(
          name = "inventory",
          joinColumns = @JoinColumn(name = "household_id"),
          inverseJoinColumns = @JoinColumn(name = "item_id")
  )
  private List<Item> inventory;

  @Column
  private int unregisteredAdultCount;

  @Column
  private int unregisteredChildCount;

  @Column
  private int unregisteredPetCount;

  /**
   * Blank constructor for the Household model
   */
  public Household() {
    unregisteredAdultCount = 0;
    unregisteredChildCount = 0;
    unregisteredPetCount = 0;
  }


  /**
   * Constructor for the Household model
   * @param id of the household
   * @param name of the household
   * @param latitude of the household
   * @param longitude of the household
   */
  public Household(int id, String name, double latitude, double longitude) {
    this.id = id;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    unregisteredAdultCount = 0;
    unregisteredChildCount = 0;
    unregisteredPetCount = 0;
  }


  /**
   * Constructor for the Household model
   * @param id of the household
   * @param name of the household
   * @param latitude of the household
   * @param longitude of the household
   * @param members of the household
   * @param inventory of the household
   */
  public Household(int id, String name, double latitude, double longitude, List<HouseholdMembers> members, List<Item> inventory) {
    this.id = id;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.members = members;
    this.inventory = inventory;
    unregisteredAdultCount = 0;
    unregisteredChildCount = 0;
    unregisteredPetCount = 0;
  }
}
