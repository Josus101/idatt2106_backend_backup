package org.ntnu.idatt2106.backend.model.map;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a coordinate with latitude and longitude.
 * It is used to represent the coordinates of a point on the map.
 *
 * @author André Merkesdal
 * @version 0.2
 * @since 0.1
 */
@Entity
@Table(name = "coordinate")
@Getter
@Setter
public class Coordinate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private double latitude;

  @Column(nullable = false)
  private double longitude;

  @OneToOne(mappedBy = "coordinatePoint", cascade = CascadeType.ALL, orphanRemoval = true)
  private MapEntity mapEntity;

  /**
   * Blank constructor for the Coordinate model
   */
  public Coordinate() {}

  /**
   * Constructor for the Coordinate model
   *
   * @param latitude  of the coordinate
   * @param longitude of the coordinate
   */
  public Coordinate(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
