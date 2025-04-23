package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

  public Admin() {};

  public Admin(
          int id,
          String username,
          String password,
          boolean isSuper) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.isSuper = isSuper;
  }
}
