package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Type model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
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

  /**
   * Blank constructor for the Type model
   */
  public Type() {}

  /**
   * Constructor for the Type model
   * @param id of the type
   * @param name of the type
   * @param services the services that are of this type
   */
  public Type(int id, String name, List<EmergencyService> services) {
    this.id = id;
    this.name = name;
    this.services = services;
  }

  /**
   * Constructor for the Type model
   * @param id of the type
   * @param name of the type
   */
  public Type(int id, String name) {
    this(id, name, null);
  }

  /**
   * Constructor for the Type model
   * @param name of the type
   */
  public Type(String name) {
    this(0, name, null);
  }

}
