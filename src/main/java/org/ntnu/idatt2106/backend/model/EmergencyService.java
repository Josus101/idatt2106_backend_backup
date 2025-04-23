package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

  // Constructors
  public EmergencyService() {}

  public EmergencyService(String name, Double latitude, Double longitude, Type type) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.type = type;
  }
}
