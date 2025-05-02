package org.ntnu.idatt2106.backend.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * User model for the database
 * @Author Konrad Seime
 * @since 0.1
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String firstname;

  @Column(nullable = false)
  private String lastname;

  @Column(unique = true, nullable = false)
  private String phoneNumber;

  @Column
  private double latitude;

  @Column
  private double longitude;

  @Column()
  private boolean verified;

  @OneToMany(mappedBy = "user", orphanRemoval = true)
  private List<HouseholdMembers> householdMemberships = new ArrayList<>();



  /**
   * Blank Constructor for the User model
   */
  public User() {};


  /**
   * Constructor for the User model
   * @param id of the user
   * @param email of the user
   * @param password of the user
   * @param firstname of the user
   * @param lastname of the user
   * @param phoneNumber of the user
   * @param latitude of the user
   * @param longitude of the user
   */
  public User(int id, String email, String password, String firstname, String lastname, String phoneNumber, double latitude, double longitude, boolean verified) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.firstname = firstname;
    this.lastname = lastname;
    this.phoneNumber = phoneNumber;
    this.latitude = latitude;
    this.longitude = longitude;
    this.verified = verified;
  }


  /**
   * Constructor for the User model
   * @param email of the user
   * @param password of the user
   * @param firstname of the user
   * @param lastname of the user
   * @param phoneNumber of the user
   */
  public User(String email, String password, String firstname, String lastname, String phoneNumber) {
    this(0, email, password, firstname, lastname, phoneNumber, 0.0, 0.0, false);
  }


  /**
   * Constructor for the User model
   * @param id of the user
   * @param email of the user
   * @param password of the user
   * @param firstname of the user
   * @param lastname of the user
   * @param phoneNumber of the user
   */
  public User(int id, String email, String password, String firstname, String lastname, String phoneNumber) {
    this(id, email, password, firstname, lastname, phoneNumber, 0.0, 0.0, false);
  }


  /**
   * Get method that returns the id of the user object as a {@code String}
   * @return user is as a {@code String}
   */
  public String getStringID() {
    return String.valueOf(id);
  }

  /**
   * ToString method for the user
   * @return first- and last name of the user
   */
  @Override
  public String toString() {
    return firstname + " " + lastname;
  }

}
