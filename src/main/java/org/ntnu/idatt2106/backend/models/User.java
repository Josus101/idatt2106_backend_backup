package org.ntnu.idatt2106.backend.models;

// java util
import java.util.List;
import java.util.Map;

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
          int id,
          String email,
          String password,
          String firstname,
          String lastname,
          String phoneNumber,
          double latitude,
          double longitude,
          List<HouseholdMembers> householdMemberships) {
    this.id = id;
    this.email = email;
    this.firstname = firstname;
    this.lastname = lastname;
    this.phoneNumber = phoneNumber;
    this.latitude = latitude;
    this.longitude = longitude;
    this.householdMemberships = householdMemberships;
  }


}
