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
  private String englishName;

  @Column(nullable = false, unique = true)
  private String norwegianName;

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
   * @param englishName of the category
   * @param kcalPerUnit of the category (nullable)
   * @param isEssential whether the category is essential
   */
  public Category(int id, String englishName, String norwegianName, Integer kcalPerUnit, Boolean isEssential) {
    this.id = id;
    this.englishName = englishName;
    this.norwegianName = norwegianName;
    this.kcalPerUnit = kcalPerUnit;
    this.isEssential = isEssential;
  }

  /**
   * Constructor for the Category model
   * @param englishName of the category
   * @param kcalPerUnit of the category (nullable)
   * @param isEssential whether the category is essential
   */
  public Category(String englishName, String norwegianName, Integer kcalPerUnit, Boolean isEssential) { // Changed int to Integer
    this.englishName = englishName;
    this.norwegianName = norwegianName;
    this.kcalPerUnit = kcalPerUnit;
    this.isEssential = isEssential;
  }
}

