package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Table(name = "admin")
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

  public String getStringID() {
    return String.valueOf(id);
  }
  @Override
  public String toString() {
    return username;
  }
}
