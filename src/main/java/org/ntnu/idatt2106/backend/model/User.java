package org.ntnu.idatt2106.backend.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import java.util.TimeZone;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idatt2106.backend.dto.user.UserPositionUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

  @Column
  private Date positionUpdateTime = new Date();

  @Column()
  private boolean verified;

  @OneToMany(mappedBy = "user", orphanRemoval = true)
  private List<HouseholdMembers> householdMemberships = new ArrayList<>();

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private UserSettings userSettings;



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

  /**
   * Set the position of the user
   * @param positionUpdate the new position of the user
   */
  public void setPosition(UserPositionUpdate positionUpdate) {
    double latitude = positionUpdate.getLatitude();
    double longitude = positionUpdate.getLongitude();
    if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException("Invalid latitude or longitude");
    }
    this.setLongitude(longitude);
    this.setLatitude(latitude);
    this.setPositionUpdateTime(new Date(System.currentTimeMillis()));
  }

  /**
   * Get formatted time of last position update
   *
   * @return formatted time of last position update
   */
  public String getFormattedPositionUpdateTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    formatter.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
    return formatter.format(positionUpdateTime);
  }

  /**
   * Get the primary household of the user
   * @return the primary household of the user
   */
  public Household getPrimaryHousehold() {
    return householdMemberships.stream()
            .filter(HouseholdMembers::isPrimary)
            .map(HouseholdMembers::getHousehold)
            .findFirst()
            .orElse(null);
  }

  /**
   * Get the households of the user
   * @return the households of the user
   */
  public String[] getHouseholdMembershipsString() {
    String[] householdNames = new String[householdMemberships.size()];
    for (int i = 0; i < householdMemberships.size(); i++) {
      householdNames[i] = householdMemberships.get(i).getHousehold().getName();
    }
    return householdNames;
  }
}
