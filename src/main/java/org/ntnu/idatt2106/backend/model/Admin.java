package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
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
  @Column(unique = true, nullable = false)
  private String username;
  @Column(nullable = false)
  private String password;
  @Column(nullable = false)
  private boolean isSuperUser;


  /**
   * Blank constructor for the Admin model
   */
  public Admin() {};


  /**
   * Constructor for the Admin model.
   * @param username of the admin
   * @param password of the admin
   * @param isSuperUser flag to set the super admin status of the admin
   */
  public Admin(String username, String password, boolean isSuperUser) {
    this.username = username;
    this.password = password;
    this.isSuperUser = isSuperUser;
  }

  public String getStringID() {
    return String.valueOf(id);
  }

  @Override
  public String toString() {
    return username;
  }
}
