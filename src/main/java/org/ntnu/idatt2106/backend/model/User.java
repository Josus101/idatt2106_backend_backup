package org.ntnu.idatt2106.backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

  public User() {}

  public User(String email, String password, String firstname, String surname, String phoneNumber) {
    this.email = email;
    this.password = password;
    this.firstname = firstname;
    this.surname = surname;
    this.phoneNumber = phoneNumber;
  }

  public String getStringID() {
    return String.valueOf(id);
  }
  @Override
  public String toString() {
    return firstname + " " + surname;
  }
}


