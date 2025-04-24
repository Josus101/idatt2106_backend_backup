package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
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

  @OneToMany(mappedBy = "household")
  private List<HouseholdMembers> members;

  @ManyToMany
  @JoinTable(
          name = "inventory",
          joinColumns = @JoinColumn(name = "household_id"),
          inverseJoinColumns = @JoinColumn(name = "item_id")
  )
  private List<Item> inventory;

  /**
   * Blank constructor for the Household model
   */
  public Household() {}


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
  }
}
