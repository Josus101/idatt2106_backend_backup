package org.ntnu.idatt2106.backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
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

}
