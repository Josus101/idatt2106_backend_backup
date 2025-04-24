package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Admin model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "admin")
@Getter
@Setter
public class Admin {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private boolean isSuper;


  /**
   * Blank constructor for the Admin model
   */
  public Admin() {};
  

  /**
   * Constructor for the Admin model.
   * @param id of the admin entry
   * @param username of the admin
   * @param password of the admin
   * @param isSuper flag to set the super admin status of the admin
   */
  public Admin(int id, String username, String password, boolean isSuper) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.isSuper = isSuper;
  }
}
