package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * EmergencyService model for the database
 * @Author Jonas Reiher
 * @since 0.1
 */
@Entity
@Table(name = "emergencyService")
@Getter
@Setter
public class EmergencyService {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private double latitude;

  @Column(nullable = false)
  private double longitude;

  @ManyToOne
  @JoinColumn(name = "type", nullable = false)
  private Type type;

  /**
   * Blank constructor for the EmergencyService model
   */
  public EmergencyService() {}


  /**
   * Constructor for the EmergencyService model
   * @param name of the emergencyService
   * @param latitude of the emergencyService
   * @param longitude of the emergencyService
   * @param type of the emergencyService
   */
  public EmergencyService(String name, Double latitude, Double longitude, Type type) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.type = type;
  }
}
