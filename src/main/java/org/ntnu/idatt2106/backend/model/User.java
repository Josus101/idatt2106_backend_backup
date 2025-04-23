package org.ntnu.idatt2106.backend.model;

// java util
import java.util.List;

// jpa
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

  @OneToMany(mappedBy = "user")
  private List<HouseholdMembers> householdMemberships;

  public User() {};

  public User(
          String email,
          String password,
          String firstname,
          String lastname,
          String phoneNumber) {
    this.email = email;
    this.password = password;
    this.firstname = firstname;
    this.lastname = lastname;
    this.phoneNumber = phoneNumber;
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
