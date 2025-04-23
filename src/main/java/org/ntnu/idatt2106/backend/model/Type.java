package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "type")
@Getter
@Setter
public class Type {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "type")
  private List<EmergencyService> services;

  public Type() {}
  public Type(
          String name,
          List<EmergencyService> services) {
    this.name = name;
    this.services = services;
  }

}
