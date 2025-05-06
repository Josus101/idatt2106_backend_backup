package org.ntnu.idatt2106.backend.model.map;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for zones table in the database.
 * This class is used to represent a zone on the map.
 *
 * @author André Merkesdal
 * @since 0.1
 */
@Entity
@Table(name = "map_zone")
@Getter
@Setter
public class MapZone {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "map_shared_id_seq")
  @SequenceGenerator(name = "map_shared_id_seq", sequenceName = "map_shared_id_sequence", allocationSize = 1)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private int severityLevel;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "centered_coordinate_id", referencedColumnName = "id")
  private Coordinate coordinatePoint;

  @OneToMany(mappedBy = "mapZone", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CoordinatePolygon> polygons = new ArrayList<>();

  /**
   * Blank constructor for the MapZone model
   */
  public MapZone() {}
  /**
   * Constructor for the MapZone model
   *
   * @param name          of the zone
   * @param description   of the zone
   * @param address       of the zone
   * @param type          of the zone
   * @param severityLevel of the zone
   *                      * @param centeredCoordinate of the zon
   */
  public MapZone(String name, String description, String address, Coordinate coordinatePoint, String type,
                 int severityLevel) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.type = type;
    this.severityLevel = severityLevel;
    this.coordinatePoint = coordinatePoint;
  }
}
