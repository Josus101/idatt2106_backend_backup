package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


/**
 * Item model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "item")
@Getter
@Setter
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private double amount;

  @ManyToOne
  @JoinColumn(name = "unit_id", nullable = false)
  private Unit unit;

  @Column(nullable = false)
  private Date expirationDate;

  @ManyToMany
  @JoinTable(
          name = "inventory",
          joinColumns = @JoinColumn(name = "item_id"),
          inverseJoinColumns = @JoinColumn(name = "household_id")
  )
  private List<Household> household;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  /**
   * Blank constructor for the Item model
   */
  public Item() {};

  /**
   * Constructor for the item model
   * @param id of the item
   * @param name of the item
   * @param amount of the item
   * @param unit of the item
   * @param expirationDate of the item
   */
  public Item(int id, String name, double amount, Unit unit, Date expirationDate) {
    this.id = id;
    this.name = name;
    this.amount = amount;
    this.unit = unit;
    this.expirationDate = expirationDate;
  }
  /**
   * Constructor for the item model
   * @param id of the item
   * @param name of the item
   * @param amount of the item
   * @param unit of the item
   * @param expirationDate of the item
   * @param category of the item
   */
  public Item(int id, String name, double amount, Unit unit, Date expirationDate, Category category) {
    this.id = id;
    this.name = name;
    this.amount = amount;
    this.unit = unit;
    this.expirationDate = expirationDate;
    this.category = category;
  }
  /**
   * Constructor for the item model
   * @param name of the item
   * @param amount of the item
   * @param unit of the item
   * @param expirationDate of the item
   */
  public Item(String name, double amount, Unit unit, Date expirationDate, Category category) {
    this.name = name;
    this.amount = amount;
    this.unit = unit;
    this.expirationDate = expirationDate;
    this.category = category;
  }
}
