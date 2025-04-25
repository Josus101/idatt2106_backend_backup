package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Category model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, unique = true)
  private String name;


  /**
   * Blank constructor for the Category model
   */
  public Category() {};

  /**
   * Constructor for the Category model
   * @param id of the category
   * @param name of the category
   */
  public Category(int id, String name) {
    this.id = id;
    this.name = name;
  }
}
