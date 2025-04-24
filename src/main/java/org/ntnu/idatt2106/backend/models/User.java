package org.ntnu.idatt2106.backend.models;
import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idatt2106.backend.models.HouseholdMembers;

@Getter
@Setter
@Entity
@Table(name = "users")
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
  private String surname;

  @Column(unique = true, nullable = false)
  private String phoneNumber;

  @Column()
  private float latitude;

  @Column()
  private float longitude;

  @Column()
  private boolean verified;

  @OneToMany(mappedBy = "user")
  private List<HouseholdMembers> householdMemberships;


  public User() {}

  public User(String email, String password, String firstname, String surname, String phoneNumber) {
    this.email = email;
    this.password = password;
    this.firstname = firstname;
    this.surname = surname;
    this.phoneNumber = phoneNumber;
    this.verified = false;
  }

  /**
   * Adds a household membership to the users list of memberships.
   * @param householdMembership the household membership to add
   */
  public void addHouseholdMembership(HouseholdMembers householdMembership) {
    this.householdMemberships.add(householdMembership);
  }

  public String getStringID() {
    return String.valueOf(id);
  }
  @Override
  public String toString() {
    return firstname + " " + surname;
  }
}


