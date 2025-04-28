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

  @Column
  private Integer kcalPerUnit;

  @Column(nullable = false)
  private Boolean isEssential;


  /**
   * Blank constructor for the Category model
   */
  public Category() {};

  /**
   * Constructor for the Category model
   * @param id of the category
   * @param name of the category
   * @param kcalPerUnit of the category (nullable)
   * @param isEssential whether the category is essential
   */
  public Category(int id, String name, Integer kcalPerUnit, Boolean isEssential) { // Changed int to Integer
    this.id = id;
    this.name = name;
    this.kcalPerUnit = kcalPerUnit;
    this.isEssential = isEssential;
  }

  /**
   * Constructor for the Category model
   * @param name of the category
   * @param kcalPerUnit of the category (nullable)
   * @param isEssential whether the category is essential
   */
  public Category(String name, Integer kcalPerUnit, Boolean isEssential) { // Changed int to Integer
    this.name = name;
    this.kcalPerUnit = kcalPerUnit;
    this.isEssential = isEssential;
  }
}

